package ru.flamexander.spring.security.jwt.controllers.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.flamexander.spring.security.jwt.dtos.CategoriesDTO;
import ru.flamexander.spring.security.jwt.dtos.VacancyDto;
import ru.flamexander.spring.security.jwt.entities.Category;
import ru.flamexander.spring.security.jwt.entities.Vacancy;
import ru.flamexander.spring.security.jwt.repositories.CategoryRepository;
import ru.flamexander.spring.security.jwt.service.CategoryService;
import ru.flamexander.spring.security.jwt.service.VacancyService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MVCCategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private final CategoryRepository categoryRepository;

    @Autowired
    private final VacancyService vacancyService;

    @GetMapping("/view-all-categories")
    public String getAllCategories(Model model) {
        List<Category> categories = categoryRepository.findAllWithVacancies();
        model.addAttribute("categories", categories);
        return "categories/view-all-categories";
    }

    @GetMapping("/add-category")
    public String showCategoriesAddPage() {
        return "categories/add-category";
    }

    @PostMapping("/add-category")
    public String addCategory(
            @Valid @ModelAttribute CategoriesDTO categoryDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "categories/add-category";
        }

        CategoryService categoryService = new CategoryService(); // Или через @Autowired
        categoryService.createNewCategory(categoryDto);
        return "redirect:/view-all-categories";
    }


    @GetMapping("/add-vacancy") // Добавлен GET-метод для отображения формы
    public String showAddVacancyForm(Model model) {
        model.addAttribute("vacancyDto", new VacancyDto());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "categories/add-vacancy";
    }

    @PostMapping("/add-vacancy")
    public String handleAddVacancy(@Valid @ModelAttribute("vacancyDto") VacancyDto vacancyDto,
                                   BindingResult bindingResult,
                                   Model model) {

        // Явная проверка categoryId
        if (vacancyDto.getCategoryId() == null) {
            bindingResult.rejectValue("categoryId", "error.categoryId", "Категория обязательна");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "categories/add-vacancy";
        }

        try {
            vacancyService.createVacancy(vacancyDto);
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при создании вакансии");
            model.addAttribute("categories", categoryService.getAllCategories());
            return "categories/add-vacancy";
        }

        return "redirect:/view-all-categories";
    }

    @GetMapping("/update-vacancy/{id}")
    public String showEditVacancyForm(@PathVariable Long id, Model model) {
        VacancyDto vacancyDto = vacancyService.getVacancyById(id);
        model.addAttribute("vacancyDto", vacancyDto);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "categories/update-vacancy";
    }

    @PostMapping("/update-vacancy/{id}")
    public String handleEditVacancy(@Valid @ModelAttribute("vacancyDto") VacancyDto vacancyDto,
                                    BindingResult bindingResult,
                                    Model model) {

        // Явная проверка categoryId
        if (vacancyDto.getCategoryId() == null) {
            bindingResult.rejectValue("categoryId", "error.categoryId", "Категория обязательна");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "categories/update-vacancy";
        }

        try {
            vacancyService.updateVacancy(vacancyDto.getId(), vacancyDto);
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при обновлении вакансии");
            model.addAttribute("categories", categoryService.getAllCategories());
            return "categories/view-all-categories";
        }

        return "redirect:/view-all-categories";
    }

    @GetMapping("/job_openings")
    public String searchVacancies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(name = "category", required = false) String categoryTitle,
            @RequestParam(name = "query", required = false) String searchQuery,
            Model model) {

        List<Category> categories = categoryService.getAllCategoriesWithVacancies();
        model.addAttribute("categories", categories);

        Pageable pageable = PageRequest.of(page, size);
        Page<Vacancy> vacancyPage;

        if ((categoryTitle != null && !categoryTitle.isEmpty()) || (searchQuery != null && !searchQuery.isEmpty())) {
            vacancyPage = vacancyService.searchVacancies(categoryTitle, searchQuery, pageable);
        } else {
            vacancyPage = vacancyService.getAllVacancies(pageable);
        }

        model.addAttribute("vacancies", vacancyPage.getContent());
        model.addAttribute("currentPage", vacancyPage.getNumber() + 1);
        model.addAttribute("totalPages", vacancyPage.getTotalPages());
        model.addAttribute("totalItems", vacancyPage.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("searchQuery", searchQuery); // Pass the search query
        model.addAttribute("categoryTitle", categoryTitle); // Pass the category title

        return "job-openings";
    }

}

