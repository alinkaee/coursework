package ru.flamexander.spring.security.jwt.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.flamexander.spring.security.jwt.entities.FavoriteVacancy;
import ru.flamexander.spring.security.jwt.entities.User;
import ru.flamexander.spring.security.jwt.entities.Vacancy;

import java.util.List;
import java.util.Optional;

public interface FavoriteVacancyRepository extends JpaRepository<FavoriteVacancy, Long> {
    Optional<FavoriteVacancy> findByUserAndVacancy(User user, Vacancy vacancy);
    void deleteByUserAndVacancy(User user, Vacancy vacancy);

    @Query("SELECT fv FROM FavoriteVacancy fv WHERE fv.user = :user")
    List<FavoriteVacancy> findByUser(@Param("user") User user);
}