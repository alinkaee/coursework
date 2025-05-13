package ru.flamexander.spring.security.jwt.utils;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public class MailUtils {
    private MailUtils(){
    }
    public static SimpleMailMessage createMailMessage( final String email,
                                                       final String subject,
                                                       final String text){
        return createMailMessage(
                Stream.of(email).toArray(String[]::new),
                subject,
                text
        );
    }

    public static SimpleMailMessage createMailMessage(final List<String> emails,
                                                      final String subject,
                                                      final String text){
        return createMailMessage(
                emails.toArray(String[]::new),
                subject,
                text
        );
    }

    private static SimpleMailMessage createMailMessage(final String[] emails,
                                                       final String subject,
                                                       final String text){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("springlibrary12@mail.ru");
        mailMessage.setTo(emails);
        mailMessage.setSubject(subject);
        mailMessage.setText(text);
        return mailMessage;
    }
    public static String buildResetPasswordMail(String username, String resetLink) {
        return String.format(
                "Dear %s,\n\n" +
                        "We received a request to reset your password. Please click the link below to proceed:\n\n" +
                        "%s\n\n" +
                        "If you did not request a password reset, please ignore this email.\n\n" +
                        "Best regards,\n" +
                        "Your Application Team",
                username, resetLink
        );
    }

}
