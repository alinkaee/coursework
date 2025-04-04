package ru.flamexander.spring.security.jwt.dtos;


import lombok.Data;

@Data
public class CategoriesDTO {
    private String title;
    private String description;

    public CategoriesDTO(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public CategoriesDTO() {}
}