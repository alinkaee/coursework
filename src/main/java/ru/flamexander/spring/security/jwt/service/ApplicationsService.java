package ru.flamexander.spring.security.jwt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ru.flamexander.spring.security.jwt.entities.Applications;
import ru.flamexander.spring.security.jwt.entities.User;
import ru.flamexander.spring.security.jwt.entities.Vacancy;
import ru.flamexander.spring.security.jwt.repositories.ApplicationsRepository;
import ru.flamexander.spring.security.jwt.repositories.UserRepository;
import ru.flamexander.spring.security.jwt.repositories.VacancyRepository;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
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
        // Проверка существования пользователя и вакансии по email и названию
        User user = userRepository.findByEmail(applicationDto.getUserEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + applicationDto.getUserEmail()));

        Vacancy vacancy = vacancyRepository.findVacancyByTitle(applicationDto.getVacancyTitle())
                .orElseThrow(() -> new RuntimeException("Vacancy not found with name: " + applicationDto.getVacancyTitle()));

        // Создаем новую заявку
        Applications application = new Applications();
        application.setUserEmail(applicationDto.getUserEmail());
        application.setVacancyTitle(applicationDto.getVacancyTitle());
        application.setDate(new Date());
        application.setStatus(applicationDto.getStatus());

        return applicationsRepository.save(application);
    }

    @Transactional
    public void applyForVacancy(String userEmail, Long vacancyId) {
        Vacancy vacancy = vacancyService.getById(vacancyId);

        if (applicationsRepository.existsByUserEmailAndVacancyTitle(userEmail, vacancy.getTitle())) {
            throw new IllegalStateException("Вы уже откликались на эту вакансию");
        }

        Applications application = new Applications();
        application.setUserEmail(userEmail);
        application.setVacancyTitle(vacancy.getTitle());
        application.setDate(new Date());
        application.setStatus("PENDING");

        applicationsRepository.save(application);
    }

    // DTO для заявки
    public static class ApplicationsDTO {
        private String userEmail; // Поле для email пользователя
        private String vacancyTitle; // Поле для названия вакансии
        private String status; // Статус заявки

        // Геттеры и сеттеры
        public String getUserEmail() {
            return userEmail;
        }

        public void setUserEmail(String userEmail) {
            this.userEmail = userEmail;
        }

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
    public Applications updateApplication(Long id, ApplicationsDTO applicationsDTO) {
        Applications existingApplication = applicationsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));

        existingApplication.setUserEmail(applicationsDTO.getUserEmail());
        existingApplication.setVacancyTitle(applicationsDTO.getVacancyTitle());
        existingApplication.setDate(new Date()); // Обновляем дату при обновлении
        existingApplication.setStatus(applicationsDTO.getStatus());

        return applicationsRepository.save(existingApplication);
    }

    // Метод для удаления заявки
    @Transactional
    public void deleteApplication(Long id) {
        applicationsRepository.deleteById(id);
    }
}



