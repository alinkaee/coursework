package ru.flamexander.spring.security.jwt.dtos;


import lombok.Data;

@Data
public class CategoriesDTO {
    private String title;
    private String description;
    private Long id;

    public CategoriesDTO(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public CategoriesDTO() {}

    public CategoriesDTO(String title, String description, Long id) {
        this.title = title;
        this.description = description;
        this.id = id;

    }
}