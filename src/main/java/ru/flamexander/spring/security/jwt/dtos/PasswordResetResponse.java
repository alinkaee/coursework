package ru.flamexander.spring.security.jwt.dtos;

public class PasswordResetResponse {
    private String message;

    public PasswordResetResponse(String message) {
        this.message = message;
    }

    // Геттеры и сеттеры
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
