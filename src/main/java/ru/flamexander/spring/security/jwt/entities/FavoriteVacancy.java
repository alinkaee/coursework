package ru.flamexander.spring.security.jwt.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "favorite_vacancies")
public class FavoriteVacancy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "vacancy_id")
    private Vacancy vacancy;
}
