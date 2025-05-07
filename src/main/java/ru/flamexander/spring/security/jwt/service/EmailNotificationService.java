package ru.flamexander.spring.security.jwt.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailNotificationService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendStatusChangeNotification(String toEmail, String vacancyTitle, String newStatus) {
        Context context = new Context();
        context.setVariable("vacancyTitle", vacancyTitle);
        context.setVariable("newStatus", newStatus);

        String emailContent = templateEngine.process("email/status-change", context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

        try {
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Изменение статуса вашей заявки");
            helper.setText(emailContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Ошибка при отправке уведомления", e);
        }

        try {
            // ... существующий код ...
            log.info("Отправлено уведомление об изменении статуса на {} для вакансии {}",
                    newStatus, vacancyTitle);
        } catch (Exception e) {
            log.error("Ошибка отправки уведомления для {}: {}", toEmail, e.getMessage());
            throw e;
        }
    }
}
