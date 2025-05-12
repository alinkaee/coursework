package ru.flamexander.spring.security.jwt.configs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@Slf4j
public class EmailConfig {

    @Value("${spring.mail.host}")
    private String mailServerHost;
    @Value("${spring.mail.port}")
    private Integer mailServerPort;
    @Value("${spring.mail.username}")
    private String mailServerUserName;
    @Value("${spring.mail.password}")
    private String mailServerPassword;

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailServerHost);
        mailSender.setPort(mailServerPort);
        mailSender.setUsername(mailServerUserName);
        mailSender.setPassword(mailServerPassword);

        Properties properties = mailSender.getJavaMailProperties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "false");
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.ssl.trust", "smtp.mail.ru");
        properties.put("mail.debug", "true");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.socketFactory.fallback", "false");
        properties.put("mail.smtp.connectiontimeout", 5000);
        properties.put("mail.smtp.timeout", 5000);
        properties.put("mail.smtp.writetimeout", 5000);

        log.info("Конфигурация почтового сервера: {}:{}", mailServerHost, mailServerPort);
        log.info("Почтовый логин: {}", mailServerUserName);

        return mailSender;
    }

}
