package ru.flamexander.spring.security.jwt.dtos;

public class PasswordResetRequest {
    private String email;
    private String newPassword;
    private String token;

    // Геттеры и сеттеры
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}

