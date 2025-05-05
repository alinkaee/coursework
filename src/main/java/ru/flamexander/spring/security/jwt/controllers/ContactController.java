package ru.flamexander.spring.security.jwt.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.flamexander.spring.security.jwt.dtos.ContactFormDto;
import ru.flamexander.spring.security.jwt.service.EmailService;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ContactController {

    private static final Logger logger = LoggerFactory.getLogger(ContactController.class);

    private final EmailService emailService;

    public ContactController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/contact")
    public ResponseEntity<Map<String, String>> handleContactForm(@RequestBody ContactFormDto formData) {
        logger.info("Получена новая заявка от {} ({})",
                formData.getName(),
                formData.getEmail());

        try {
            emailService.sendContactEmail(formData);
            logger.info("Заявка от {} успешно обработана", formData.getEmail());
            return ResponseEntity.ok()
                    .body(Collections.singletonMap("status", "success")); // Добавляем JSON-тело
        } catch (Exception e) {
            logger.error("ОШИБКА ОБРАБОТКИ ЗАЯВКИ от {}: {}",
                    formData.getEmail(),
                    e.getMessage(),
                    e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}
