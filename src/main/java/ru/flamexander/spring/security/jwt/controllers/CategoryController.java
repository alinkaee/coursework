package ru.flamexander.spring.security.jwt.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import ru.flamexander.spring.security.jwt.dtos.CategoriesDTO;
import ru.flamexander.spring.security.jwt.entities.Category;
import ru.flamexander.spring.security.jwt.service.ApplicationsService;
import ru.flamexander.spring.security.jwt.service.CategoryService;
import ru.flamexander.spring.security.jwt.service.VacancyService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final VacancyService vacancyService;

    @GetMapping("/main/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return categoryService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    public ModelAndView createCategory(
            @Valid @ModelAttribute CategoriesDTO categoryDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        List<String> errors = new ArrayList<>();

        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error -> {
                errors.add(error.getDefaultMessage());
            });
            redirectAttributes.addFlashAttribute("errors", errors);
            return new ModelAndView(new RedirectView("/add-category"));
        }

        categoryService.createNewCategory(categoryDto);
        return new ModelAndView(new RedirectView("/view-all-categories"));
    }



//    @PutMapping("/{id}")
//    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody CategoriesDTO categoryDto) {
//        try {
//            return ResponseEntity.ok(categoryService.updateCategory(id, categoryDto));
//        } catch (RuntimeException e) {
//            return ResponseEntity.notFound().build();
//        }
//    }

//    @GetMapping("/{id}/edit")
//    public String showEditCategoryForm(@PathVariable Long id, Model model) {
//        Category category = categoryService.findById(id)
//                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
//
//        CategoriesDTO categoryDto = new CategoriesDTO(category.getTitle(), category.getDescription(), category.getId());
//        model.addAttribute("categoryDto", categoryDto);
//        return "edit-category";
//    }
//
//    @PostMapping("/{id}")
//    public String updateCategory(@PathVariable Long id, @ModelAttribute("categoryDto") @Valid CategoriesDTO categoryDto, BindingResult bindingResult, Model model) {
//        if (bindingResult.hasErrors()) {
//            return "edit-category";
//        }
//        categoryService.updateCategory(id, categoryDto);
//        return "redirect:/categories";
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/by-name/{name}")
    public ResponseEntity<Category> getCategoryByName(@PathVariable String name) {
        return categoryService.findByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}