package ru.flamexander.spring.security.jwt.controllers.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.flamexander.spring.security.jwt.dtos.CategoriesDTO;
import ru.flamexander.spring.security.jwt.dtos.VacancyDto;
import ru.flamexander.spring.security.jwt.entities.Category;
import ru.flamexander.spring.security.jwt.entities.User;
import ru.flamexander.spring.security.jwt.entities.Vacancy;
import ru.flamexander.spring.security.jwt.repositories.CategoryRepository;
import ru.flamexander.spring.security.jwt.service.CategoryService;
import ru.flamexander.spring.security.jwt.service.UserService;
import ru.flamexander.spring.security.jwt.service.VacancyService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MVCCategoryController {

    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;
    private final VacancyService vacancyService;
    private final UserService userService;

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
        categoryService.createNewCategory(categoryDto);
        return "redirect:/view-all-categories";
    }

    @GetMapping("/add-vacancy")
    public String showAddVacancyForm(Model model) {
        model.addAttribute("vacancyDto", new VacancyDto());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "categories/add-vacancy";
    }

    @PostMapping("/add-vacancy")
    public String handleAddVacancy(@Valid @ModelAttribute("vacancyDto") VacancyDto vacancyDto,
                                   BindingResult bindingResult,
                                   Model model) {
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

    @GetMapping("/update-category/{id}")
    public String showEditCategoryForm(@PathVariable Long id, Model model) {
        Category category = categoryService.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        CategoriesDTO categoryDto = new CategoriesDTO(category.getTitle(), category.getDescription(), category.getId());
        model.addAttribute("categoryDto", categoryDto);
        return "categories/update-category";
    }

    @PostMapping("/update-category/{id}")
    public String updateCategory(@PathVariable Long id, @ModelAttribute("categoryDto") @Valid CategoriesDTO categoryDto,
                                 BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "categories/update-category";
        }
        categoryService.updateCategory(id, categoryDto);
        return "redirect:/view-all-categories";
    }

    @GetMapping("/job_openings")
    public String searchVacancies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(name = "category", required = false) String categoryTitle,
            @RequestParam(name = "query", required = false) String searchQuery,
            @RequestParam(name = "sort", defaultValue = "none") String sort,
            Model model) {

        List<Category> categories = categoryService.getAllCategoriesWithVacancies();
        model.addAttribute("categories", categories);
        model.addAttribute("sort", sort);

        Pageable pageable;
        if ("asc".equals(sort)) {
            pageable = PageRequest.of(page, size, Sort.by("salary").ascending());
        } else if ("desc".equals(sort)) {
            pageable = PageRequest.of(page, size, Sort.by("salary").descending());
        } else {
            pageable = PageRequest.of(page, size);
        }

        Page<Vacancy> vacancyPage = vacancyService.searchVacancies(categoryTitle, searchQuery, pageable);

        User currentUser = userService.getCurrentUser();
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("userService", userService);
        model.addAttribute("vacancies", vacancyPage.getContent());
        model.addAttribute("currentPage", vacancyPage.getNumber() + 1);
        model.addAttribute("totalPages", vacancyPage.getTotalPages());
        model.addAttribute("totalItems", vacancyPage.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("categoryTitle", categoryTitle);
        model.addAttribute("sort", sort);

        return "job-openings";
    }
}