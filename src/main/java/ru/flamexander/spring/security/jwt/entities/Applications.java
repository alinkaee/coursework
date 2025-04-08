package ru.flamexander.spring.security.jwt.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "applications")
@Data
@AllArgsConstructor
@Getter
@Setter
public class Applications {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "vacancy_name", nullable = false)
    private String vacancyTitle;

    @Column(name = "application_date")
    private Date date;

    @Column(name = "status")
    private String status;

    // Конструкторы и геттеры/сеттеры
    public Applications() {}

    public Applications(String userEmail, String vacancyTitle) {
        this.userEmail = userEmail;
        this.vacancyTitle = vacancyTitle;
    }

}


