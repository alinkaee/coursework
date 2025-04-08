package ru.flamexander.spring.security.jwt.controllers.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.flamexander.spring.security.jwt.entities.Applications;
import ru.flamexander.spring.security.jwt.entities.User;
import ru.flamexander.spring.security.jwt.entities.Vacancy;
import ru.flamexander.spring.security.jwt.repositories.ApplicationsRepository;
import ru.flamexander.spring.security.jwt.service.ApplicationsService;
import ru.flamexander.spring.security.jwt.service.UserService;
import ru.flamexander.spring.security.jwt.utils.JwtTokenUtils;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MVCUserController {

    private final ApplicationsRepository applicationsRepository;

    private final UserService userService;

    private final ApplicationsService applicationsService;



    @GetMapping("/profile")
    public String getProfile(Model model) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Applications> applications = applicationsService.getApplicationsByUserEmail(userEmail);
        model.addAttribute("applications", applications);

        User currentUser = userService.getCurrentUser();
        if (currentUser != null) {
            model.addAttribute("userName", currentUser.getUsername());
            model.addAttribute("userEmail", currentUser.getEmail());
            List<Vacancy> favoriteVacancies = userService.getFavoriteVacancies(currentUser);
            model.addAttribute("favoriteVacancies", favoriteVacancies);
        }

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

    @GetMapping("/profile_editing")
    public String getEditingProfilePage(Model model) {
        User user = userService.getCurrentUser();

        // Добавляем объект в модель
        model.addAttribute("user", user);
        return "user/profile-editing";
    }

    @PostMapping("/edit-profile")
    public String processEditProfile(@Valid @ModelAttribute("user") User user, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "profile-editing"; // Возврат на страницу редактирования при ошибках
        }

        // Сохраняем изменения
        userService.save(user);

        // Добавляем объект для редиректа
        redirectAttributes.addFlashAttribute("user", user);
        return "redirect:/edit-profile";
    }


}
