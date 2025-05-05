package ru.flamexander.spring.security.jwt.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactFormDto {
    private String name;
    private String email;
    private String phone;
    private String message;
}