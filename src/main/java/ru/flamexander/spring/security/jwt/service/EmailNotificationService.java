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
    private final EmailService emailService;

    public void sendStatusChangeNotification(String userEmail, String vacancyTitle, String oldStatus, String newStatus) throws EmailSendingException {
        emailService.sendStatusChangeNotification(userEmail, vacancyTitle, oldStatus, newStatus);
    }
}

