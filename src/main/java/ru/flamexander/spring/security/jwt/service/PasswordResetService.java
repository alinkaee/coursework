package ru.flamexander.spring.security.jwt.service;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.flamexander.spring.security.jwt.entities.PasswordResetToken;
import ru.flamexander.spring.security.jwt.entities.User;
import ru.flamexander.spring.security.jwt.exceptions.InvalidTokenException;
import ru.flamexander.spring.security.jwt.exceptions.TokenExpiredException;
import ru.flamexander.spring.security.jwt.exceptions.UserNotFoundException;
import ru.flamexander.spring.security.jwt.repositories.PasswordResetTokenRepository;
import ru.flamexander.spring.security.jwt.repositories.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import java.util.UUID;

@Service
public class PasswordResetService {
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(PasswordResetService.class);

    @Value("${spring.mail.username}")
    private String mailFrom;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${server.port}")
    private String serverPort;

    @Autowired
    public PasswordResetService(UserRepository userRepository,
                                PasswordResetTokenRepository passwordResetTokenRepository,
                                JavaMailSender mailSender,
                                PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void initiatePasswordReset(String email) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("User not found"));

            String token = UUID.randomUUID().toString();
            createPasswordResetToken(user, token);
            sendPasswordResetEmail(user.getEmail(), token);

        } catch (MailException e) {
            logger.error("Ошибка отправки email", e);
            throw new RuntimeException("Ошибка отправки email", e);
        }
    }

    private void createPasswordResetToken(User user, String token) {
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        passwordResetTokenRepository.save(resetToken);
    }

    private void sendPasswordResetEmail(String email, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailFrom);
            message.setTo(email);
            message.setSubject("Восстановление пароля");

            String resetUrl = "http://localhost:" + serverPort + "/reset-password?token=" + token;
            String emailText = "Для восстановления пароля перейдите по ссылке:\n" + resetUrl;

            message.setText(emailText);

            System.out.println("Пытаюсь отправить письмо на: " + email);
            System.out.println("Текст письма: " + emailText);

            mailSender.send(message);
            System.out.println("Письмо успешно отправлено");
        } catch (Exception e) {
            System.err.println("Ошибка при отправке письма:");
            e.printStackTrace();
            throw new RuntimeException("Не удалось отправить письмо", e);
        }
    }

    @Transactional
    public User validatePasswordResetToken(String token) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Недействительная ссылка для сброса пароля"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            passwordResetTokenRepository.delete(resetToken);
            throw new TokenExpiredException("Срок действия ссылки истек. Пожалуйста, запросите новую.");
        }

        return resetToken.getUser();
    }

    @Transactional
    public void completePasswordReset(String token, String newPassword) {
        User user = validatePasswordResetToken(token);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        passwordResetTokenRepository.deleteByToken(token);
    }
}