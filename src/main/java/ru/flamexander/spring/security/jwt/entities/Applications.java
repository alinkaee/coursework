package ru.flamexander.spring.security.jwt.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
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

    @ManyToOne(fetch = FetchType.LAZY) // Связь многие-к-одному
    @JoinColumn(name = "user_email", referencedColumnName = "email", nullable = false)
    private User user; // Добавляем ссылку на пользователя

    @Column(name = "vacancy_name", nullable = false)
    private String vacancyTitle;

    @Column(name = "application_date")
    private Date date;

    @Column(name = "status")
    private String status;

//    @Column(name = "comments", nullable = true)
//    private String comments;

    // Конструкторы и геттеры/сеттеры
    public Applications() {}

    public Applications(User user, String vacancyTitle) {
        this.user = user;
        this.vacancyTitle = vacancyTitle;
    }

}


