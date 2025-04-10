package ru.flamexander.spring.security.jwt.entities;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "avatar_filename", nullable = true)
    private String avatarFilename; // Имя файла аватарки

    @Transient // Не сохраняется в БД
    private MultipartFile avatarFile;

    @Column(name = "skills", nullable = true)
    private String skills;

    @Column(name = "resume", nullable = true)
    private String resumeFilename;

    @Column(name = "description", nullable = true)
    private String description;

    @Transient
    private MultipartFile resumeFile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<UserRole> userRoles;
}


