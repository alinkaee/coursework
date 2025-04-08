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

    @Column(name = "avatar_filename")
    private String avatarFilename; // Имя файла аватарки

    @Transient // Не сохраняется в БД
    private MultipartFile avatarFile;

    @OneToMany(mappedBy = "user")
    private Collection<UserRole> userRoles;
}


