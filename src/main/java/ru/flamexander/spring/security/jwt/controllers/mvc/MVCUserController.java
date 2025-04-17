package ru.flamexander.spring.security.jwt.controllers.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.flamexander.spring.security.jwt.dtos.UserUpdateDto;
import ru.flamexander.spring.security.jwt.entities.Applications;
import ru.flamexander.spring.security.jwt.entities.User;
import ru.flamexander.spring.security.jwt.entities.Vacancy;
import ru.flamexander.spring.security.jwt.service.ApplicationsService;
import ru.flamexander.spring.security.jwt.service.UserService;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MVCUserController {

    private final ApplicationsService applicationsService;
    private final UserService userService;

    @GetMapping("/profile")
    public String getProfile(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        // Получаем текущего пользователя
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login"; // Перенаправляем на страницу входа, если пользователь не найден
        }

        // Получаем заявки пользователя с пагинацией
        Page<Applications> applicationsPage = applicationsService.getApplicationsByUser(currentUser, page, size);

        // Добавляем заявки и данные пагинации в модель
        model.addAttribute("applications", applicationsPage.getContent()); // Список заявок на текущей странице
        model.addAttribute("currentPage", page); // Текущая страница
        model.addAttribute("totalPages", applicationsPage.getTotalPages()); // Общее количество страниц

        // Добавляем данные пользователя в модель
        model.addAttribute("userName", currentUser.getUsername());
        model.addAttribute("userEmail", currentUser.getEmail());
        model.addAttribute("favoriteVacancies", userService.getFavoriteVacancies(currentUser));

        return "user/profile";
    }

    @PostMapping("/add_application")
    public String addApplication(
            @RequestParam("vacancyTitle") String vacancyTitle,
            @RequestParam("status") String status,
            Model model
    ) {
        // Получаем текущего пользователя
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login"; // Перенаправляем на страницу входа, если пользователь не найден
        }

        // Создание DTO для заявки
        ApplicationsService.ApplicationsDTO applicationDto = new ApplicationsService.ApplicationsDTO();
        applicationDto.setUserId(currentUser.getId()); // Устанавливаем ID пользователя
        applicationDto.setVacancyTitle(vacancyTitle);
        applicationDto.setStatus(status);

        // Сохранение в базе данных
        try {
            applicationsService.createApplication(applicationDto);
            model.addAttribute("successMessage", "Заявка успешно создана!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Ошибка при создании заявки: " + e.getMessage());
        }

        // После сохранения перенаправляем на страницу профиля
        return "redirect:/profile";
    }

    @GetMapping("/profile_editing")
    public String getEditingProfilePage(Model model) {
        User user = userService.getCurrentUser();
        if (user == null) {
            return "redirect:/login"; // Перенаправляем на страницу входа, если пользователь не найден
        }

        // Добавляем объект в модель
        model.addAttribute("user", user);
        return "user/profile-editing";
    }

    /**
     * Обработка формы редактирования профиля.
     */
    @PostMapping("/edit-profile")
    public String processEditProfile(
            @Valid @ModelAttribute("user") UserUpdateDto userUpdateDto,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        // Проверка на наличие ошибок валидации
        if (result.hasErrors()) {
            return "user/profile-editing"; // Возвращаемся на страницу редактирования при ошибках
        }

        // Получаем текущего пользователя
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("error", "Пользователь не авторизован");
            return "redirect:/login";
        }

        try {
            // Обновляем данные пользователя
            userService.updateUser(currentUser.getId(), userUpdateDto);

            // Добавляем сообщение об успехе
            redirectAttributes.addFlashAttribute("success", "Профиль успешно обновлен!");
        } catch (Exception e) {
            // Добавляем сообщение об ошибке
            redirectAttributes.addFlashAttribute("error", "Ошибка при обновлении профиля: " + e.getMessage());
        }

        // Перенаправляем на страницу профиля
        return "redirect:/profile";
    }

    @GetMapping("/added_application")
    public String showUserApplicationsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Page<Applications> applications = applicationsService.getApplications(page, size);

        // Добавляем объект в модель
        model.addAttribute("applications", applications);
        model.addAttribute("currentPage", page);
        return "user/added-applications";
    }
}