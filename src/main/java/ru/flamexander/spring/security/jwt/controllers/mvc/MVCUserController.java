package ru.flamexander.spring.security.jwt.controllers.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.flamexander.spring.security.jwt.entities.Applications;
import ru.flamexander.spring.security.jwt.repositories.ApplicationsRepository;
import ru.flamexander.spring.security.jwt.service.ApplicationsService;
import ru.flamexander.spring.security.jwt.utils.JwtTokenUtils;

import java.util.Date;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MVCUserController {

    private final ApplicationsRepository applicationsRepository;

    private final ApplicationsService applicationsService;

    private final JwtTokenUtils jwtTokenUtils;


    @GetMapping("/profile")
    public String getProfile(@CookieValue(name = "jwt_token", required = false) String token, Model model) {
        if (token != null) {
            String email = jwtTokenUtils.getUsername(token);
            List<Applications> applications = applicationsService.getApplicationsByUserEmail(email);
            model.addAttribute("applications", applications);
            model.addAttribute("userEmail", email);
        }
        return "user/profile";
    }

    private String getCurrentUserEmail() {
        // Здесь должна быть логика получения email текущего пользователя, например, из Spring Security
        return "pochta@mail.ru"; // Замените на реальный email пользователя
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
