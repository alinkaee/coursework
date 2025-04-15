package ru.flamexander.spring.security.jwt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.flamexander.spring.security.jwt.dtos.VacancyDto;
import ru.flamexander.spring.security.jwt.entities.Applications;
import ru.flamexander.spring.security.jwt.entities.Category;
import ru.flamexander.spring.security.jwt.entities.User;
import ru.flamexander.spring.security.jwt.entities.Vacancy;
import ru.flamexander.spring.security.jwt.repositories.ApplicationsRepository;
import ru.flamexander.spring.security.jwt.repositories.CategoryRepository;
import ru.flamexander.spring.security.jwt.repositories.VacancyRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VacancyService {

    private final VacancyRepository vacancyRepository;
    private final ApplicationsRepository applicationsRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;

    public Vacancy getById(Long id) {
        return vacancyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Вакансия не найдена"));
    }

    public Optional<Vacancy> findById(Long id) {
        return vacancyRepository.findById(id);
    }

    @Transactional
    public VacancyDto createVacancy(VacancyDto vacancyDto) {
        Vacancy vacancy = new Vacancy();
        vacancy.setTitle(vacancyDto.getTitle());
        vacancy.setDescription(vacancyDto.getDescription());
        vacancy.setSalary(vacancyDto.getSalary());
        vacancy.setCategory(categoryRepository.findById(vacancyDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found")));

        Vacancy savedVacancy = vacancyRepository.save(vacancy);
        return convertToDto(savedVacancy);
    }

    @Transactional
    public VacancyDto updateVacancy(Long id, VacancyDto vacancyDto) {
        // Находим существующую вакансию
        Vacancy vacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vacancy not found with id: " + id));

        // Обновляем основные поля
        vacancy.setTitle(vacancyDto.getTitle());
        vacancy.setDescription(vacancyDto.getDescription());
        vacancy.setSalary(vacancyDto.getSalary());
        vacancy.setCategory(vacancy.getCategory());

        // Обновляем категорию, если она указана
        if (vacancyDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(vacancyDto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Category not found with id: " + vacancyDto.getCategoryId()));
            vacancy.setCategory(category);
        }

        // Сохраняем обновленную вакансию
        Vacancy updatedVacancy = vacancyRepository.save(vacancy);
        return convertToDto(updatedVacancy);
    }




    private VacancyDto mapToDto(Vacancy vacancy) {
        VacancyDto dto = new VacancyDto();
        dto.setId(vacancy.getId());
        dto.setTitle(vacancy.getTitle());
        dto.setDescription(vacancy.getDescription());
        dto.setSalary(vacancy.getSalary());
        if (vacancy.getCategory() != null) {
            dto.setCategoryId(vacancy.getCategory().getId());
        }
        return dto;
    }
    public List<VacancyDto> getAllVacancy() {
        return vacancyRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Page<Vacancy> getAllVacancies(Pageable pageable) {
        return vacancyRepository.findAll(pageable);
    }

    public VacancyDto getVacancyById(Long id) {
        Vacancy vacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vacancy not found with id: " + id));
        return convertToDto(vacancy);
    }
    @Transactional
    public void deleteVacancy(Long id) {
        if (!vacancyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vacancy not found with id: " + id);
        }
        vacancyRepository.deleteById(id);
    }
    public List<VacancyDto> searchVacancy(String name) {
        return vacancyRepository.findByTitleContainingIgnoreCase(name).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Page<Vacancy> searchVacancies(String categoryTitle, String searchQuery, Pageable pageable) {
        if ((categoryTitle == null || categoryTitle.isEmpty()) && (searchQuery == null || searchQuery.isEmpty())) {
            // Если нет фильтров, просто возвращаем все вакансии с пагинацией и сортировкой
            return vacancyRepository.findAll(pageable);
        }

        if (categoryTitle != null && !categoryTitle.isEmpty() && (searchQuery == null || searchQuery.isEmpty())) {
            // Фильтрация по категории
            Category category = categoryService.getCategoryByTitleOrThrow(categoryTitle);
            if (category != null) {
                return vacancyRepository.findByCategory(category, pageable);
            } else {
                return Page.empty(pageable);
            }
        }

        if ((categoryTitle == null || categoryTitle.isEmpty()) && searchQuery != null && !searchQuery.isEmpty()) {
            // Фильтрация по названию вакансии
            return vacancyRepository.findByTitleContainingIgnoreCase(searchQuery, pageable);
        }

        // Фильтрация по категории и названию вакансии
        Category category = categoryService.getCategoryByTitleOrThrow(categoryTitle);
        if (category == null) {
            return Page.empty(pageable);
        }

        return vacancyRepository.findByCategoryAndTitleContainingIgnoreCase(category, searchQuery, pageable);
    }

    public List<VacancyDto> getAllVacanciesSortedBySalaryAsc() {
        return vacancyRepository.findAllOrderBySalaryAsc().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<VacancyDto> getAllVacanciesSortedBySalaryDesc() {
        return vacancyRepository.findAllOrderBySalaryDesc().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private VacancyDto convertToDto(Vacancy vacancy) {
        VacancyDto dto = new VacancyDto();
        dto.setId(vacancy.getId());
        dto.setTitle(vacancy.getTitle());  // Изменил setName() на setTitle()
        dto.setDescription(vacancy.getDescription());
        dto.setSalary(vacancy.getSalary());

        // Безопасное получение ID категории
        if (vacancy.getCategory() != null) {
            dto.setCategoryId(vacancy.getCategory().getId());
        }

        return dto;
    }

}