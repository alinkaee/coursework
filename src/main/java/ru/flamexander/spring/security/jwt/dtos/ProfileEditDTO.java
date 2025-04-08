package ru.flamexander.spring.security.jwt.dtos;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ProfileEditDTO {
    private String firstName;
    private String lastName;
    private String phone;
    private String description;
    private String skills;
    private MultipartFile avatarFile;
}
