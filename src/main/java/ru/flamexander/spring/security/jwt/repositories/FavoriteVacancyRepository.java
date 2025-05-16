package ru.flamexander.spring.security.jwt.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Modifying
    @Query("DELETE FROM FavoriteVacancy f WHERE f.user.id = :userId AND f.vacancy.id = :vacancyId")
    int deleteByUserAndVacancy(@Param("userId") Long userId, @Param("vacancyId") Long vacancyId);

    @Query("SELECT f FROM FavoriteVacancy f WHERE f.user.id = :userId AND f.vacancy.id = :vacancyId")
    Optional<FavoriteVacancy> findByUserAndVacancyIds(@Param("userId") Long userId, @Param("vacancyId") Long vacancyId);
}