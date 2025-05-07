package ru.flamexander.spring.security.jwt.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import ru.flamexander.spring.security.jwt.dtos.ContactFormDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.internet.MimeMessage;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Value("${spring.mail.username}")
    private String fromEmail;

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender, SpringTemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendContactEmail(ContactFormDto formData) {
        logger.info("Попытка отправки письма на pochtaqwe65@gmail.com с адреса {}", fromEmail);
        logger.debug("Данные формы: Имя={}, Email={}, Телефон={}, Длина сообщения={} символов",
                formData.getName(),
                formData.getEmail(),
                formData.getPhone(),
                formData.getMessage().length());

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo("pochtaqwe65@gmail.com");
            message.setSubject("Новая заявка с сайта от " + formData.getName());

            String text = String.format(
                    "Имя: %s\nEmail: %s\nТелефон: %s\n\nСообщение:\n%s",
                    formData.getName(),
                    formData.getEmail(),
                    formData.getPhone(),
                    formData.getMessage()
            );

            message.setText(text);

            logger.debug("Сформировано письмо: Тема='{}', Размер текста={} символов",
                    message.getSubject(), text.length());

            mailSender.send(message);
            logger.info("Письмо успешно отправлено на pochtaqwe65@gmail.com");

        } catch (org.springframework.mail.MailAuthenticationException e) {
            logger.error("ОШИБКА АУТЕНТИФИКАЦИИ: Неверные учетные данные почтового сервера. Проверьте логин/пароль в настройках", e);
            throw new RuntimeException("Ошибка авторизации на почтовом сервере", e);

        } catch (org.springframework.mail.MailSendException e) {
            logger.error("ОШИБКА ОТПРАВКИ: Не удалось отправить письмо. Причины:", e);
            if (e.getFailedMessages() != null && !e.getFailedMessages().isEmpty()) {
                e.getFailedMessages().forEach((address, ex) ->
                        logger.error("Не удалось отправить на {}: {}", address, ex.getMessage()));
            }
            throw new RuntimeException("Ошибка при отправке письма", e);

        } catch (Exception e) {
            logger.error("НЕИЗВЕСТНАЯ ОШИБКА: При отправке письма произошла непредвиденная ошибка", e);
            throw new RuntimeException("Неизвестная ошибка при отправке", e);
        }
    }

    private final SpringTemplateEngine templateEngine;

}