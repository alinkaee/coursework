package ru.flamexander.spring.security.jwt.controllers.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public String getProfile(Model model) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Applications> applications = applicationsService.getApplicationsByUserEmail(userEmail);
        model.addAttribute("applications", applications);
        return "user/profile";
    }

    @PostMapping("/add_application")
    public String AddApplication(
            @RequestParam("userEmail") String userEmail,
            @RequestParam("vacancyTitle") String vacancyTitle,
            @RequestParam("status") String status,
            Model model
    ) {
        // Создание сущности Applications
        Applications application = new Applications();
        application.setUserEmail(userEmail);
        application.setVacancyTitle(vacancyTitle);
        application.setStatus(status);
        application.setDate(new Date()); // Установка текущей даты

        // Сохранение в базе данных
        applicationsRepository.save(application);

        // После сохранения перенаправляем на страницу профиля
        return "redirect:/profile";
    }

}
