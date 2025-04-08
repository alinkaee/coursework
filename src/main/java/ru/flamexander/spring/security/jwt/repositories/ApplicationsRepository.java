package ru.flamexander.spring.security.jwt.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.flamexander.spring.security.jwt.entities.Applications;

import java.util.Date;
import java.util.List;

@Repository
public interface ApplicationsRepository extends JpaRepository<Applications, Long> {

        // Пример метода поиска заявок по статусу
        List<Applications> findByStatus(String status);

        boolean existsByUserEmailAndVacancyTitle(String userEmail, String vacancyTitle);

        // Пример метода поиска заявок по дате (>=)
        List<Applications> findByDateGreaterThanEqual(Date date);

        // Пример метода поиска заявок по дате (<=)
        List<Applications> findByDateLessThanEqual(Date date);


        // Пример метода поиска заявок по дате и статусу
        List<Applications> findByDateBetweenAndStatus(Date startDate, Date endDate, String status);

        // Метод для поиска заявок по email пользователя
        List<Applications> findByUserEmail(String userEmail);

        // Метод для поиска заявок по названию вакансии
        List<Applications> findByVacancyTitle(String vacancyTitle);
}

