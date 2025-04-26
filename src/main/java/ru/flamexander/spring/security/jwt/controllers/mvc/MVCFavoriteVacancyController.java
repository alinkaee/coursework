package ru.flamexander.spring.security.jwt.controllers.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.flamexander.spring.security.jwt.entities.User;
import ru.flamexander.spring.security.jwt.entities.Vacancy;
import ru.flamexander.spring.security.jwt.service.FavoriteVacancyService;
import ru.flamexander.spring.security.jwt.service.UserService;
import ru.flamexander.spring.security.jwt.service.VacancyService;

import java.security.Principal;

@Controller
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class MVCFavoriteVacancyController {
    private final FavoriteVacancyService favoriteVacancyService;
    private final VacancyService vacancyService;
    private final UserService userService;


    @PostMapping("/remove/{vacancyId}")
    public String removeFromFavorites(
            @PathVariable Long vacancyId,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getName();
        User user = userService.findByEmail(email)
                .orElseThrow(() -> {
                    System.err.println("User not found with email: " + email);
                    return new UsernameNotFoundException("User not found");
                });

        Vacancy vacancy = vacancyService.getById(vacancyId);
        favoriteVacancyService.removeVacancyFromFavorites(user, vacancy);

        System.out.println("Principal email: " + principal.getName());
        User user1 = userService.findByEmail(principal.getName())
                .orElseThrow(() -> {
                    System.err.println("Ошибка: пользователь с email '" + principal.getName() + "' не найден");
                    return new UsernameNotFoundException("User not found");
                });

        redirectAttributes.addFlashAttribute("success", "Вакансия удалена из избранного");
        return "redirect:/profile#favorites";
    }
}
