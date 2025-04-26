package ru.flamexander.spring.security.jwt.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.flamexander.spring.security.jwt.entities.Category;
import ru.flamexander.spring.security.jwt.repositories.CategoryRepository;
import ru.flamexander.spring.security.jwt.dtos.CategoriesDTO;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);
    private CategoryRepository categoryRepository;

    @Autowired
    public void setCategoryRepository(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findAll() {
        logger.info("Запрос всех категорий");
        List<Category> categories = categoryRepository.findAll();
        logger.info("Найдено {} категорий", categories.size());
        return categories;
    }

    public List<Category> getAllCategories() {
        logger.info("Получение списка всех категорий");
        return findAll();
    }

    public Optional<Category> findById(Long id) {
        logger.info("Поиск категории по ID: {}", id);
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            logger.info("Категория с ID {} найдена", id);
        } else {
            logger.warn("Категория с ID {} не найдена", id);
        }
        return category;
    }

    public Optional<Category> findByName(String name) {
        logger.info("Поиск категории по названию: {}", name);
        Optional<Category> category = categoryRepository.findByTitle(name);
        if (category.isPresent()) {
            logger.info("Категория '{}' найдена", name);
        } else {
            logger.warn("Категория '{}' не найдена", name);
        }
        return category;
    }

    @Transactional
    public Category createNewCategory(CategoriesDTO categoryDto) {
        logger.info("Создание новой категории: {}", categoryDto.getTitle());
        Category category = new Category();
        category.setTitle(categoryDto.getTitle());
        category.setDescription(categoryDto.getDescription());
        Category savedCategory = categoryRepository.save(category);
        logger.info("Новая категория создана с ID: {}", savedCategory.getId());
        return savedCategory;
    }

    @Transactional
    public Category updateCategory(Long id, CategoriesDTO categoryDto) {
        logger.info("Обновление категории ID: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Ошибка обновления: категория с ID {} не найдена", id);
                    return new RuntimeException("Категория не найдена с id: " + id);
                });

        logger.info("Обновление данных категории: новое название='{}', описание='{}'",
                categoryDto.getTitle(), categoryDto.getDescription());
        category.setTitle(categoryDto.getTitle());
        category.setDescription(categoryDto.getDescription());

        Category updatedCategory = categoryRepository.save(category);
        logger.info("Категория ID {} успешно обновлена", id);
        return updatedCategory;
    }

    @Transactional
    public void deleteCategory(Long id) {
        logger.info("Удаление категории с ID: {}", id);
        categoryRepository.deleteById(id);
        logger.info("Категория с ID {} успешно удалена", id);
    }

    public Category getCategoryByTitleOrThrow(String title) {
        logger.info("Получение категории по названию: {}", title);
        return categoryRepository.findByTitle(title)
                .orElseThrow(() -> {
                    logger.error("Категория '{}' не найдена", title);
                    return new RuntimeException("Категория не найдена с названием: " + title);
                });
    }

    public List<Category> getAllCategoriesWithVacancies() {
        logger.info("Запрос категорий с вакансиями");
        List<Category> categories = categoryRepository.findAllWithVacancies();
        logger.info("Найдено {} категорий с вакансиями", categories.size());
        return categories;
    }
}