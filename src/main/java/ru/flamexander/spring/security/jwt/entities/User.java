package ru.flamexander.spring.security.jwt.entities;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

@Entity
@Data
//@NoArgsConstructor
@Table(name = "users")
@Setter
public class User implements Serializable { // Добавляем реализацию Serializable
    private static final long serialVersionUID = 1L; // Уникальный идентификатор для сериализации

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email", unique = true)
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

    @Transient
    private MultipartFile resumeFile;

    @Column(name = "description", nullable = true)
    private String description;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<UserRole> userRoles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<Applications> applications; // Коллекция заявок пользователя

    public User() {}

}