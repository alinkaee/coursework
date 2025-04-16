package ru.flamexander.spring.security.jwt.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.flamexander.spring.security.jwt.entities.Applications;
import ru.flamexander.spring.security.jwt.entities.User;

import java.util.Date;
import java.util.List;

@Repository
public interface ApplicationsRepository extends JpaRepository<Applications, Long> {

        // Поиск заявок по статусу
        List<Applications> findByStatus(String status);

        // Проверка существования заявки для пользователя и вакансии
        boolean existsByUserAndVacancyTitle(User user, String vacancyTitle);

        // Поиск заявок по дате (>=)
        List<Applications> findByDateGreaterThanEqual(Date date);

        // Поиск заявок по дате (<=)
        List<Applications> findByDateLessThanEqual(Date date);

        // Поиск заявок по диапазону дат и статусу
        List<Applications> findByDateBetweenAndStatus(Date startDate, Date endDate, String status);

        // Поиск всех заявок пользователя
        List<Applications> findByUser(User user);

        // Поиск заявок по названию вакансии
        List<Applications> findByVacancyTitle(String vacancyTitle);

        // Поиск заявок по пользователю и статусу
        List<Applications> findByUserAndStatus(User user, String status);
}

