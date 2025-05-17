package ru.flamexander.spring.security.jwt.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import ru.flamexander.spring.security.jwt.entities.Applications;
import ru.flamexander.spring.security.jwt.entities.User;
import ru.flamexander.spring.security.jwt.entities.Vacancy;
import ru.flamexander.spring.security.jwt.service.*;
import ru.flamexander.spring.security.jwt.utils.JwtTokenUtils;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/applications")
public class ApplicationsController {


    private static final Logger log = LoggerFactory.getLogger(ApplicationsController.class);
    private final ApplicationsService applicationsService;
    private final UserService userService;

    @Autowired
    public ApplicationsController(ApplicationsService applicationsService, UserService userService) {
        this.applicationsService = applicationsService;
        this.userService = userService;
    }

    @PostMapping("/add")
    public ModelAndView createApplication(
            @Valid @ModelAttribute ApplicationsService.ApplicationsDTO applicationDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        List<String> errors = new ArrayList<>();

        // Валидация данных
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error -> {
                errors.add(error.getDefaultMessage());
            });
            redirectAttributes.addFlashAttribute("errors", errors);
            return new ModelAndView(new RedirectView("/add-application"));
        }

        // Получаем текущего пользователя
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            errors.add("Пользователь не авторизован");
            redirectAttributes.addFlashAttribute("errors", errors);
            return new ModelAndView(new RedirectView("/add-application"));
        }

        // Устанавливаем пользователя в DTO
        applicationDto.setUserId(currentUser.getId());

        // Обработка и сохранение заявки
        try {
            applicationsService.createApplication(applicationDto);
            redirectAttributes.addFlashAttribute("successMessage", "Заявка успешно создана!");
            return new ModelAndView(new RedirectView("/profile"));
        } catch (Exception e) {
            errors.add("Ошибка при создании заявки: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errors", errors);
            return new ModelAndView(new RedirectView("/add-application"));
        }
    }

    @GetMapping("/my")
    public String getUserApplications(
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

        // Добавляем данные в модель
        model.addAttribute("applications", applicationsPage.getContent()); // Список заявок на текущей странице
        model.addAttribute("currentPage", page); // Текущая страница
        model.addAttribute("totalPages", applicationsPage.getTotalPages()); // Общее количество страниц

        return "user/profile"; // имя вашего Thymeleaf шаблона профиля
    }

    @PostMapping("/apply/{vacancyId}")
    public String applyForVacancy(
            @PathVariable Long vacancyId,
            RedirectAttributes redirectAttributes) {

        // Получаем текущего пользователя
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("error", "Пользователь не авторизован");
            return "redirect:/job_openings";
        }

        try {
            applicationsService.applyForVacancy(currentUser.getEmail(), vacancyId);
            redirectAttributes.addFlashAttribute("success", "Отклик успешно отправлен!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/job_openings";
    }

    @GetMapping("/{id}")
    public ResponseEntity<Applications> getApplicationById(@PathVariable Long id) {
        Optional<Applications> application = applicationsService.getApplicationById(id);
        return application.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/update-status/{id}")
    public String updateApplicationStatus(
            @PathVariable Long id,
            @RequestParam String status,
            RedirectAttributes redirectAttributes) {
        try {
            applicationsService.updateStatus(id, status);
            redirectAttributes.addFlashAttribute("successMessage", "Статус заявки успешно обновлен!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при обновлении статуса: " + e.getMessage());
        }

        Optional<Applications> optionalApplication = applicationsService.getApplicationById(id);
        if (optionalApplication.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Заявка не найдена");
            return "redirect:/added_application";
        }

        Applications currentApplication = optionalApplication.get();
        return "redirect:/added_application";
    }

    @GetMapping("/status-update")
    public String showUpdateStatusPage() {
        return "user/status-update";
    }



    @GetMapping
    public ResponseEntity<List<Applications>> getAllApplications() {
        List<Applications> applications = applicationsService.getAllApplications();
        return ResponseEntity.ok(applications);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Applications> updateApplication(@PathVariable Long id, @RequestBody ApplicationsService.ApplicationsDTO applicationsDto) {
        Applications updatedApplication = applicationsService.updateApplication(id, applicationsDto);
        return ResponseEntity.ok(updatedApplication);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteApplication(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            applicationsService.deleteApplication(id);
            redirectAttributes.addFlashAttribute("successMessage", "Заявка успешно удалена!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении заявки: " + e.getMessage());
        }
        return "redirect:/added_application";
    }

    @DeleteMapping("/{id}")
    public String deleteApplicationFromUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            applicationsService.deleteApplication(id);
            redirectAttributes.addFlashAttribute("successMessage", "Заявка успешно удалена!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении заявки: " + e.getMessage());
        }
        return "redirect:/profile";
    }

}