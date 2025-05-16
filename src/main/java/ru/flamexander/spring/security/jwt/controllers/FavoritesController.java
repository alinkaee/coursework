package ru.flamexander.spring.security.jwt.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.flamexander.spring.security.jwt.entities.User;
import ru.flamexander.spring.security.jwt.entities.Vacancy;
import ru.flamexander.spring.security.jwt.service.UserService;
import ru.flamexander.spring.security.jwt.service.VacancyService;

@Controller
public class FavoritesController {

    @Autowired
    private UserService userService;

    @Autowired
    private VacancyService vacancyService;

    @PostMapping("/vacancies/add-to-favorites/{vacancyId}")
    public String addVacancyToFavorites(@PathVariable Long vacancyId) {
        User currentUser = userService.getCurrentUser();
        Vacancy vacancy = vacancyService.findById(vacancyId).orElse(null);

        if (vacancy != null) {
            userService.addVacancyToFavorites(currentUser, vacancy);
        }

        return "redirect:/job_openings";
    }

//    @PostMapping("/vacancies/remove-from-favorites/{vacancyId}")
//    public String removeVacancyFromFavorites(@PathVariable Long vacancyId) {
//        User currentUser = userService.getCurrentUser();
//        Vacancy vacancy = vacancyService.findById(vacancyId).orElse(null);
//
//        if (vacancy != null) {
//            userService.removeVacancyFromFavorites(currentUser, vacancy);
//        }
//
//        return "redirect:/job_openings";
//    }
}