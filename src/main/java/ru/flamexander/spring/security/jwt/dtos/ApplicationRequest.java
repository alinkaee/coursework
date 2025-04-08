package ru.flamexander.spring.security.jwt.dtos;

public class ApplicationRequest {
    private String userEmail;
    private String vacancyTitle;
    private String status;

    public String getUserEmail() {
        return userEmail;
    }

    public String getVacancyTitle() {
        return vacancyTitle;
    }
}
