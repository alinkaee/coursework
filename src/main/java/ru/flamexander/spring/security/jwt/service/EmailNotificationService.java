package ru.flamexander.spring.security.jwt.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import ru.flamexander.spring.security.jwt.exceptions.EmailSendingException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailNotificationService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    public void sendStatusChangeNotification(String email, String vacancyTitle,
                                             String oldStatus, String newStatus)
            throws EmailSendingException {
        try {
            Context context = new Context();
            context.setVariable("vacancyTitle", vacancyTitle);
            context.setVariable("oldStatus", oldStatus);
            context.setVariable("newStatus", newStatus);

            String emailContent = templateEngine.process("email/status-changed", context);

            sendEmail(
                    email,
                    "Статус вашей заявки изменен",
                    emailContent
            );
        } catch (Exception e) {
            throw new EmailSendingException("Ошибка отправки уведомления об изменении статуса", e);
        }
    }

    private void sendEmail(String to, String subject, String content) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("noreply@recruitium.ru");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }
}

