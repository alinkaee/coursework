package ru.flamexander.spring.security.jwt.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(VacancyService.class);

    private final VacancyRepository vacancyRepository;
    private final ApplicationsRepository applicationsRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;

    public Vacancy getById(Long id) {
        logger.info("Получение вакансии по ID: {}", id);
        return vacancyRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Вакансия с ID {} не найдена", id);
                    return new ResourceNotFoundException("Вакансия не найдена");
                });
    }

    public Optional<Vacancy> findById(Long id) {
        logger.info("Поиск вакансии по ID: {}", id);
        Optional<Vacancy> vacancy = vacancyRepository.findById(id);
        if (vacancy.isEmpty()) {
            logger.warn("Вакансия с ID {} не найдена", id);
        }
        return vacancy;
    }

    @Transactional
    public VacancyDto createVacancy(VacancyDto vacancyDto) {
        logger.info("Создание новой вакансии: {}", vacancyDto.getTitle());
        Vacancy vacancy = new Vacancy();
        vacancy.setTitle(vacancyDto.getTitle());
        vacancy.setDescription(vacancyDto.getDescription());
        vacancy.setSalary(vacancyDto.getSalary());

        logger.debug("Поиск категории с ID: {}", vacancyDto.getCategoryId());
        Category category = categoryRepository.findById(vacancyDto.getCategoryId())
                .orElseThrow(() -> {
                    logger.error("Категория с ID {} не найдена", vacancyDto.getCategoryId());
                    return new RuntimeException("Category not found");
                });
        vacancy.setCategory(category);

        Vacancy savedVacancy = vacancyRepository.save(vacancy);
        logger.info("Вакансия успешно создана с ID: {}", savedVacancy.getId());
        return convertToDto(savedVacancy);
    }

    @Transactional
    public VacancyDto updateVacancy(Long id, VacancyDto vacancyDto) {
        logger.info("Обновление вакансии ID: {}", id);
        Vacancy vacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Вакансия с ID {} не найдена для обновления", id);
                    return new ResourceNotFoundException("Vacancy not found with id: " + id);
                });

        logger.debug("Обновление полей вакансии: title={}, salary={}",
                vacancyDto.getTitle(), vacancyDto.getSalary());
        vacancy.setTitle(vacancyDto.getTitle());
        vacancy.setDescription(vacancyDto.getDescription());
        vacancy.setSalary(vacancyDto.getSalary());

        if (vacancyDto.getCategoryId() != null) {
            logger.debug("Обновление категории вакансии на ID: {}", vacancyDto.getCategoryId());
            Category category = categoryRepository.findById(vacancyDto.getCategoryId())
                    .orElseThrow(() -> {
                        logger.error("Категория с ID {} не найдена", vacancyDto.getCategoryId());
                        return new ResourceNotFoundException("Category not found with id: " + vacancyDto.getCategoryId());
                    });
            vacancy.setCategory(category);
        }

        Vacancy updatedVacancy = vacancyRepository.save(vacancy);
        logger.info("Вакансия ID {} успешно обновлена", id);
        return convertToDto(updatedVacancy);
    }

    public List<VacancyDto> getAllVacancy() {
        logger.info("Запрос всех вакансий");
        List<VacancyDto> result = vacancyRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        logger.info("Найдено {} вакансий", result.size());
        return result;
    }

    public Page<Vacancy> getAllVacancies(Pageable pageable) {
        logger.info("Запрос всех вакансий с пагинацией: {}", pageable);
        return vacancyRepository.findAll(pageable);
    }

    public VacancyDto getVacancyById(Long id) {
        logger.info("Получение вакансии по ID: {}", id);
        Vacancy vacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Вакансия с ID {} не найдена", id);
                    return new ResourceNotFoundException("Vacancy not found with id: " + id);
                });
        return convertToDto(vacancy);
    }

    @Transactional
    public void deleteVacancy(Long id) {
        logger.info("Удаление вакансии с ID: {}", id);
        if (!vacancyRepository.existsById(id)) {
            logger.error("Попытка удаления несуществующей вакансии с ID: {}", id);
            throw new ResourceNotFoundException("Vacancy not found with id: " + id);
        }
        vacancyRepository.deleteById(id);
        logger.info("Вакансия с ID {} успешно удалена", id);
    }

    public List<VacancyDto> searchVacancy(String name) {
        logger.info("Поиск вакансий по названию: {}", name);
        List<VacancyDto> result = vacancyRepository.findByTitleContainingIgnoreCase(name).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        logger.info("Найдено {} вакансий по запросу '{}'", result.size(), name);
        return result;
    }

    public Page<Vacancy> searchVacancies(String categoryTitle, String searchQuery, Pageable pageable) {
        logger.info("Поиск вакансий с фильтрами: категория='{}', запрос='{}'", categoryTitle, searchQuery);

        if ((categoryTitle == null || categoryTitle.isEmpty()) && (searchQuery == null || searchQuery.isEmpty())) {
            logger.debug("Без фильтров - возврат всех вакансий");
            return vacancyRepository.findAll(pageable);
        }

        if (categoryTitle != null && !categoryTitle.isEmpty() && (searchQuery == null || searchQuery.isEmpty())) {
            logger.debug("Фильтрация по категории: {}", categoryTitle);
            Category category = categoryService.getCategoryByTitleOrThrow(categoryTitle);
            return vacancyRepository.findByCategory(category, pageable);
        }

        if ((categoryTitle == null || categoryTitle.isEmpty()) && searchQuery != null && !searchQuery.isEmpty()) {
            logger.debug("Фильтрация по названию: {}", searchQuery);
            return vacancyRepository.findByTitleContainingIgnoreCase(searchQuery, pageable);
        }

        logger.debug("Комбинированная фильтрация: категория='{}', запрос='{}'", categoryTitle, searchQuery);
        Category category = categoryService.getCategoryByTitleOrThrow(categoryTitle);
        return vacancyRepository.findByCategoryAndTitleContainingIgnoreCase(category, searchQuery, pageable);
    }

    public List<VacancyDto> getAllVacanciesSortedBySalaryAsc() {
        logger.info("Запрос вакансий с сортировкой по возрастанию зарплаты");
        List<VacancyDto> result = vacancyRepository.findAllOrderBySalaryAsc().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        logger.info("Найдено {} вакансий", result.size());
        return result;
    }

    public List<VacancyDto> getAllVacanciesSortedBySalaryDesc() {
        logger.info("Запрос вакансий с сортировкой по убыванию зарплаты");
        List<VacancyDto> result = vacancyRepository.findAllOrderBySalaryDesc().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        logger.info("Найдено {} вакансий", result.size());
        return result;
    }

    private VacancyDto convertToDto(Vacancy vacancy) {
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
}