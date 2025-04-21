package ru.flamexander.spring.security.jwt.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Data
@Getter
@Setter
public class UserUpdateDto {
    private String username;
    private String phone;
    private String email;
    private String description;
    private String skills;
    private MultipartFile avatarFile;
    private MultipartFile resumeFile;
    private String resumeFilename;
}