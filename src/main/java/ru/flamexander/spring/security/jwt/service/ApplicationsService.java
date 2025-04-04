package ru.flamexander.spring.security.jwt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.flamexander.spring.security.jwt.dtos.ApplicationsDTO;
import ru.flamexander.spring.security.jwt.dtos.CategoriesDTO;
import ru.flamexander.spring.security.jwt.dtos.VacancyDto;
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

//@RequiredArgsConstructor
@Service
public class ApplicationsService {

    private final ApplicationsRepository applicationsRepository;
    private final VacancyRepository vacancyRepository;
    private final UserRepository userRepository;

    @Autowired
    public ApplicationsService(ApplicationsRepository applicationsRepository,
                               VacancyRepository vacancyRepository,
                               UserRepository userRepository) {
        this.applicationsRepository = applicationsRepository;
        this.vacancyRepository = vacancyRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Applications createApplication(ApplicationDto applicationDto) {
        // Получаем Vacancy по ID
        Vacancy vacancy = vacancyRepository.findById(applicationDto.getVacancyId())
                .orElseThrow(() -> new RuntimeException("Vacancy not found with id: " + applicationDto.getVacancyId()));

        // Получаем User по ID
        User user = userRepository.findById(applicationDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + applicationDto.getUserId()));

        // Создаем новую заявку и устанавливаем полученные объекты
        Applications application = new Applications();
        application.setUserId(applicationDto.getUserId());
        application.setVacancyId(applicationDto.getVacancyId());
        application.setDate(new Date()); // Устанавливаем текущую дату
        application.setStatus(applicationDto.getStatus());

        return applicationsRepository.save(application);
    }

    // DTO для заявки
    public static class ApplicationDto {
        private Long userId; // Поле для ID пользователя
        private Long vacancyId; // Поле для ID вакансии
        private String status; // Статус заявки

        // Геттеры и сеттеры
        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public Long getVacancyId() {
            return vacancyId;
        }

        public void setVacancyId(Long vacancyId) {
            this.vacancyId = vacancyId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    // Метод для получения заявки по ID
    public Optional<Applications> getApplicationById(Long id) {
        return applicationsRepository.findById(id);
    }

    // Метод для получения всех заявок
    public List<Applications> getAllApplications() {
        return applicationsRepository.findAll();
    }

    // Метод для обновления заявки
    @Transactional
    public Applications updateApplication(Long id, ApplicationDto applicationDto) {
        Applications existingApplication = applicationsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));

        existingApplication.setUserId(applicationDto.getUserId());
        existingApplication.setVacancyId(applicationDto.getVacancyId());
        existingApplication.setDate(new Date()); // Обновляем дату при обновлении
        existingApplication.setStatus(applicationDto.getStatus());

        return applicationsRepository.save(existingApplication);
    }

    // Метод для удаления заявки
    @Transactional
    public void deleteApplication(Long id) {
        applicationsRepository.deleteById(id);
    }
}


