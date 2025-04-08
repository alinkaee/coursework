package ru.flamexander.spring.security.jwt.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import ru.flamexander.spring.security.jwt.entities.Applications;
import ru.flamexander.spring.security.jwt.entities.Vacancy;
import ru.flamexander.spring.security.jwt.repositories.ApplicationsRepository;
import ru.flamexander.spring.security.jwt.service.ApplicationsService;
import ru.flamexander.spring.security.jwt.service.VacancyService;
import ru.flamexander.spring.security.jwt.utils.JwtTokenUtils;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/applications")
public class ApplicationsController {

    private final ApplicationsService applicationsService;

    @Autowired
    public ApplicationsController(ApplicationsService applicationsService, JwtTokenUtils jwtTokenUtils, VacancyService vacancyService, ApplicationsRepository applicationsRepository) {
        this.applicationsService = applicationsService;
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
    public String getUserApplications(Model model) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Applications> applications = applicationsService.getApplicationsByUserEmail(userEmail);
        model.addAttribute("applications", applications);
        return "user/profile"; // имя вашего Thymeleaf шаблона профиля
    }


    @PostMapping("/apply/{vacancyId}")
    public String applyForVacancy(
            @PathVariable Long vacancyId,
            RedirectAttributes redirectAttributes) {

        String userEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        try {
            applicationsService.applyForVacancy(userEmail, vacancyId);
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable Long id) {
        applicationsService.deleteApplication(id);
        return ResponseEntity.noContent().build();
    }
}


