package ru.flamexander.spring.security.jwt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

//@RequiredArgsConstructor
@Service
@EnableTransactionManagement
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
    public Applications createApplication(ApplicationsDTO applicationDto) {
        // Проверка существования пользователя и вакансии по email и названию
        User user = userRepository.findByEmail(applicationDto.getUserEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + applicationDto.getUserEmail()));

        Vacancy vacancy = vacancyRepository.findVacancyByTitle(applicationDto.getVacancyTitle())
                .orElseThrow(() -> new RuntimeException("Vacancy not found with name: " + applicationDto.getVacancyTitle()));

        // Создаем новую заявку
        Applications application = new Applications();
        application.setUserEmail(applicationDto.getUserEmail());
        application.setVacancyName(applicationDto.getVacancyTitle());
        application.setDate(new Date()); // Устанавливаем текущую дату
        application.setStatus(applicationDto.getStatus());

        return applicationsRepository.save(application);
    }

    // DTO для заявки
    public static class ApplicationsDTO {
        private String userEmail; // Поле для email пользователя
        private String vacancyName; // Поле для названия вакансии
        private String status; // Статус заявки

        // Геттеры и сеттеры
        public String getUserEmail() {
            return userEmail;
        }

        public void setUserEmail(String userEmail) {
            this.userEmail = userEmail;
        }

        public String getVacancyTitle() {
            return vacancyName;
        }

        public void setVacancyName(String vacancyName) {
            this.vacancyName = vacancyName;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public List<Applications> getApplicationsByUserEmail(String userEmail) {
        return applicationsRepository.findByUserEmail(userEmail);
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
    public Applications updateApplication(Long id, ApplicationsDTO applicationDto) {
        Applications existingApplication = applicationsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));

        existingApplication.setUserEmail(applicationDto.getUserEmail());
        existingApplication.setVacancyName(applicationDto.getVacancyTitle());
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



