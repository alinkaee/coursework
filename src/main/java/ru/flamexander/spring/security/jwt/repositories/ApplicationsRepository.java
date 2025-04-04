package ru.flamexander.spring.security.jwt.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.flamexander.spring.security.jwt.entities.Applications;
import ru.flamexander.spring.security.jwt.entities.Vacancy;

import java.util.Date;
import java.util.List;

@Repository
public interface ApplicationsRepository extends JpaRepository<Applications, Long> {

//        // Пример метода поиска заявок по пользователю
//        List<Applications> findByUserId(Long userId);
//
//        // Пример метода поиска заявок по вакансии
//        List<Applications> findByVacancyId(Long vacancyId);

        // Пример метода поиска заявок по статусу
        List<Applications> findByStatus(String status);

        // Пример метода поиска заявок по дате (>=)
        List<Applications> findByDateGreaterThanEqual(Date date);

        // Пример метода поиска заявок по дате (<=)
        List<Applications> findByDateLessThanEqual(Date date);


        // Пример метода поиска заявок по дате и статусу
        List<Applications> findByDateBetweenAndStatus(Date startDate, Date endDate, String status);

        // Метод для поиска заявок по email пользователя
        List<Applications> findByUserEmail(String userEmail);

        // Метод для поиска заявок по названию вакансии
        List<Applications> findByVacancyName(String vacancyName);



//        //Пример метода поиска по части имени пользователя
//        List<Applications> findByUser_UsernameContainingIgnoreCase(String username);
//
//        // Пример метода поиска по части имени вакансии
//        List<Applications> findByVacancy_NameContainingIgnoreCase(String vacancyName);
}

