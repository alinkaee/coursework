package ru.flamexander.spring.security.jwt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.flamexander.spring.security.jwt.dtos.CategoriesDTO;
import ru.flamexander.spring.security.jwt.dtos.VacancyDto;
import ru.flamexander.spring.security.jwt.entities.*;
import ru.flamexander.spring.security.jwt.repositories.*;

import java.util.Arrays;
import java.util.List;

@Component
public class AdminInitializer implements CommandLineRunner {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private VacancyService vacancyService;

    @Override
    public void run(String... args) throws Exception {
        initAdmin();
        initUser();
        initItCategoriesAndVacancies();

    }

    private void initAdmin() {
        if (!userService.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("password"));

            Role adminRole = roleService.getRoleByName("ROLE_ADMIN")
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setName("ROLE_ADMIN");
                        return roleRepository.save(newRole);
                    });

            userRoleService.createUserWithRole(admin, roleService.getRoleIdByName("ROLE_ADMIN"));
        }
    }

    private void initUser() {
        if (!userService.existsByUsername("user1")) {
            User user = new User();
            user.setUsername("user1");
            user.setEmail("user1@example.com");
            user.setPassword(passwordEncoder.encode("123QWEasd!"));

            Role userRole = roleService.getRoleByName("ROLE_USER")
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setName("ROLE_USER");
                        return roleRepository.save(newRole);
                    });

            userRoleService.createUserWithRole(user, roleService.getRoleIdByName("ROLE_USER"));
        }
    }

    private void initItCategoriesAndVacancies() {
        if (categoryService.getAllCategories().isEmpty()) {
            // Создаем IT-категории
            CategoriesDTO development = new CategoriesDTO("Разработка", "Разработка программного обеспечения");
            CategoriesDTO testing = new CategoriesDTO("Тестирование", "QA и тестирование ПО");
            CategoriesDTO devops = new CategoriesDTO("DevOps", "Развертывание и эксплуатация");
            CategoriesDTO dataScience = new CategoriesDTO("Data Science", "Анализ данных и машинное обучение");

            List<CategoriesDTO> itCategories = Arrays.asList(
                    development, testing, devops, dataScience
            );

            itCategories.forEach(categoryService::createNewCategory);

            // Создаем IT-вакансии
            createItVacancies();
        }
    }

    private void createItVacancies() {
        // Вакансии в разработке
        createVacancy("Разработка", "Java-разработчик (Middle/Senior)",
                "Разработка backend-приложений на Spring Framework, участие в проектировании архитектуры", 180000);
        createVacancy("Разработка", "Frontend-разработчик (React)",
                "Разработка SPA-приложений на React.js, работа с Redux/MobX", 150000);
        createVacancy("Разработка", "Fullstack разработчик (Node.js + React)",
                "Полный цикл разработки веб-приложений", 200000);

        // Вакансии в тестировании
        createVacancy("Тестирование", "QA Engineer (Manual)",
                "Ручное тестирование веб и мобильных приложений, составление тест-кейсов", 90000);
        createVacancy("Тестирование", "Automation QA (Java)",
                "Автоматизация тестирования на Selenium, JUnit", 140000);

        // DevOps вакансии
        createVacancy("DevOps", "DevOps Engineer",
                "Настройка CI/CD (GitLab CI), работа с Kubernetes и Docker", 220000);
        createVacancy("DevOps", "System Administrator",
                "Администрирование Linux-серверов, мониторинг и поддержка", 120000);

        // Data Science вакансии
        createVacancy("Data Science", "Data Analyst",
                "Анализ данных, построение отчетов в Power BI/Tableau", 150000);
        createVacancy("Data Science", "Machine Learning Engineer",
                "Разработка и внедрение ML-моделей", 210000);
    }

    private void createVacancy(String categoryName, String title, String description, Integer salary) {
        Category category = categoryService.getCategoryByTitleOrThrow(categoryName);

        VacancyDto vacancyDto = new VacancyDto();
        vacancyDto.setTitle(title);
        vacancyDto.setDescription(description);
        vacancyDto.setSalary(salary);
        vacancyDto.setCategoryId(category.getId());

        vacancyService.createVacancy(vacancyDto);
    }
}