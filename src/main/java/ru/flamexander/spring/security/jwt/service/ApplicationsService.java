package ru.flamexander.spring.security.jwt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ru.flamexander.spring.security.jwt.entities.Applications;
import ru.flamexander.spring.security.jwt.entities.User;
import ru.flamexander.spring.security.jwt.entities.Vacancy;
import ru.flamexander.spring.security.jwt.repositories.ApplicationsRepository;
import ru.flamexander.spring.security.jwt.repositories.UserRepository;
import ru.flamexander.spring.security.jwt.repositories.VacancyRepository;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
@EnableTransactionManagement
public class ApplicationsService {

    private final ApplicationsRepository applicationsRepository;
    private final VacancyRepository vacancyRepository;
    private final UserRepository userRepository;
    private final VacancyService vacancyService;

    @Autowired
    public ApplicationsService(ApplicationsRepository applicationsRepository,
                               VacancyRepository vacancyRepository,
                               UserRepository userRepository, VacancyService vacancyService) {
        this.applicationsRepository = applicationsRepository;
        this.vacancyRepository = vacancyRepository;
        this.userRepository = userRepository;
        this.vacancyService = vacancyService;
    }

    @Transactional
    public Applications createApplication(ApplicationsDTO applicationDto) {
        // Находим пользователя по ID
        User user = userRepository.findById(applicationDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + applicationDto.getUserId()));

        // Находим вакансию по названию
        Vacancy vacancy = vacancyRepository.findVacancyByTitle(applicationDto.getVacancyTitle())
                .orElseThrow(() -> new RuntimeException("Vacancy not found with name: " + applicationDto.getVacancyTitle()));

        // Проверяем, что пользователь не откликался на эту вакансию ранее
        if (applicationsRepository.existsByUserAndVacancyTitle(user, vacancy.getTitle())) {
            throw new IllegalStateException("Вы уже откликались на эту вакансию");
        }

        // Создаем новую заявку
        Applications application = new Applications();
        application.setUser(user);
        application.setVacancyTitle(vacancy.getTitle());
        application.setDate(new Date());
        application.setStatus(applicationDto.getStatus());

        return applicationsRepository.save(application);
    }

    public void applyForVacancy(String userEmail, Long vacancyId) {
        // Находим пользователя по email
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        // Находим вакансию по ID
        Vacancy vacancy = vacancyService.getById(vacancyId);

        // Проверяем, что пользователь не откликался на эту вакансию ранее
        if (applicationsRepository.existsByUserAndVacancyTitle(user, vacancy.getTitle())) {
            throw new IllegalStateException("Вы уже откликались на эту вакансию");
        }

        // Создаем новую заявку
        Applications application = new Applications();
        application.setUser(user);
        application.setVacancyTitle(vacancy.getTitle());
        application.setDate(new Date());
        application.setStatus("На рассмотрении");

        applicationsRepository.save(application);
    }

    public List<Applications> getApplicationsByUser(User user) {
        return applicationsRepository.findByUser(user);
    }

    /**
     * Получение заявки по ID.
     */
    public Optional<Applications> getApplicationById(Long id) {
        return applicationsRepository.findById(id);
    }

    /**
     * Получение всех заявок.
     */
    public List<Applications> getAllApplications() {
        return applicationsRepository.findAll();
    }

    /**
     * Удаление заявки.
     */
    public void deleteApplication(Long id) {
        applicationsRepository.deleteById(id);
    }

    /**
     * Получение пагинированных заявок.
     */
    public Page<Applications> getApplications(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return applicationsRepository.findAll(pageable);
    }

    /**
     * DTO для заявки.
     */
    public static class ApplicationsDTO {
        private Long userId; // Email пользователя
        private String vacancyTitle; // Название вакансии
        private String status; // Статус заявки

        // Геттеры и сеттеры

        public String getVacancyTitle() {
            return vacancyTitle;
        }

        public void setVacancyTitle(String vacancyTitle) {
            this.vacancyTitle = vacancyTitle;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public void setUserId(Long id) {
            this.userId = userId;
        }

        public Long getUserId() {
            return userId;
        }
    }

    // Метод для обновления заявки
    public Applications updateApplication(Long id, ApplicationsDTO applicationsDTO) {
        Applications existingApplication = applicationsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found with id: " + id));

        // Находим пользователя по email
        User user = userRepository.findById(applicationsDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + applicationsDTO.getUserId()));

        // Обновляем данные заявки
        existingApplication.setUser(user);
        existingApplication.setVacancyTitle(applicationsDTO.getVacancyTitle());
        existingApplication.setDate(new Date()); // Обновляем дату при изменении
        existingApplication.setStatus(applicationsDTO.getStatus());

        return applicationsRepository.save(existingApplication);
    }
}



