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

//    @Column(name = "phone")
//    private String phone;
//
//    @Column(name = "avatar_filename", nullable = false)
//    private String avatarFilename; // Имя файла аватарки
//
//    @Transient // Не сохраняется в БД
//    private MultipartFile avatarFile;
//
//    @Column(name = "skills")
//    private String skills;

//    @Column(name = "resume")
//    private String resumeFilename;
//
//    @Transient
//    private MultipartFile resumeFile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<UserRole> userRoles;
}


