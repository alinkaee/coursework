package ru.flamexander.spring.security.jwt.dtos;

public class ApplicationRequest {
    private String userEmail;
    private String vacancyName;
    private String status;

    public String getUserEmail() {
        return userEmail;
    }

    public String getVacancyName() {
        return vacancyName;
    }
}
