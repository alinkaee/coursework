package ru.flamexander.spring.security.jwt.dtos;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserUpdateDto {
    private String username;
    private String email;
    private String phone;
    private String description;
    private String skills;
    private MultipartFile avatar;
    private MultipartFile resume;
}