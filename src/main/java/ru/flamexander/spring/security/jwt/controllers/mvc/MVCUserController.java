package ru.flamexander.spring.security.jwt.controllers.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.flamexander.spring.security.jwt.entities.Applications;
import ru.flamexander.spring.security.jwt.repositories.ApplicationsRepository;

import java.util.Date;

@Controller
@RequiredArgsConstructor
public class MVCUserController {

    private final ApplicationsRepository applicationsRepository;

    @GetMapping("/profile")
    public String getProfilePage() {
        return "user/profile";
    }

    @GetMapping("/add_application")
    public String showAddedApplication() {
        return "user/add-application";
    }

    @PostMapping("/add_application")
    public String AddApplication(
            @RequestParam("userEmail") String userEmail,
            @RequestParam("vacancyName") String vacancyName,
            @RequestParam("status") String status,
            Model model
    ) {
        // Создание сущности Applications
        Applications application = new Applications();
        application.setUserEmail(userEmail);
        application.setVacancyName(vacancyName);
        application.setStatus(status);
        application.setDate(new Date()); // Установка текущей даты

        // Сохранение в базе данных
        applicationsRepository.save(application);

        // После сохранения перенаправляем на страницу профиля
        return "redirect:/profile";
    }

}
