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
        initRealUsers();
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

    private void initRealUsers() {
        createRealUser("ivan_petrov", "ivan.petrov@example.com", "+79161234567",
                "Java, Spring, SQL", "Опыт разработки на Java более 5 лет");
        createRealUser("anna_smirnova", "anna.smirnova@example.com", "+79162345678",
                "JavaScript, React, Redux", "Frontend разработчик с опытом работы 3 года");
        createRealUser("sergey_ivanov", "sergey.ivanov@example.com", "+79163456789",
                "Python, Django, Flask", "Backend разработчик на Python");
        createRealUser("elena_kuznetsova", "elena.kuznetsova@example.com", "+79164567890",
                "QA, Selenium, JUnit", "Инженер по автоматизированному тестированию");
        createRealUser("dmitry_sokolov", "dmitry.sokolov@example.com", "+79165678901",
                "DevOps, Docker, Kubernetes", "DevOps инженер с опытом настройки CI/CD");
        createRealUser("olga_vorobeva", "olga.vorobeva@example.com", "+79166789012",
                "Data Science, Machine Learning", "Специалист по машинному обучению");
        createRealUser("alexey_fedorov",  "alexey.fedorov@example.com", "+79167890123",
                "C#, .NET, ASP.NET", "Разработчик на платформе .NET");
        createRealUser("maria_volkova", "maria.volkova@example.com", "+79168901234",
                "UI/UX Design, Figma, Adobe XD", "Дизайнер интерфейсов");
        createRealUser("andrey_morozov", "andrey.morozov@example.com", "+79169012345",
                "PHP, Laravel, MySQL", "Fullstack разработчик на PHP");
        createRealUser("ekaterina_pavlova",  "ekaterina.pavlova@example.com", "+79160123456",
                "Android, Kotlin, Java", "Мобильный разработчик под Android");
    }

    private void createRealUser(String username, String email, String phone,
                                String skills, String description) {
        if (!userService.existsByUsername(username)) {
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPhone(phone);
            user.setSkills(skills);
            user.setDescription(description);
            user.setPassword(passwordEncoder.encode("PAssword123!")); // Стандартный пароль для всех тестовых пользователей

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
            // Создаем IT-категории (10 категорий)
            List<CategoriesDTO> itCategories = Arrays.asList(
                    new CategoriesDTO("Разработка", "Разработка программного обеспечения"),
                    new CategoriesDTO("Тестирование", "QA и тестирование ПО"),
                    new CategoriesDTO("DevOps", "Развертывание и эксплуатация"),
                    new CategoriesDTO("Data Science", "Анализ данных и машинное обучение"),
                    new CategoriesDTO("Кибербезопасность", "Защита информации и безопасность"),
                    new CategoriesDTO("Управление проектами", "PM и руководство IT-проектами"),
                    new CategoriesDTO("Аналитика", "Бизнес- и системная аналитика"),
                    new CategoriesDTO("Дизайн", "UI/UX и графический дизайн"),
                    new CategoriesDTO("Администрирование", "Системное администрирование"),
                    new CategoriesDTO("Техподдержка", "IT-поддержка и helpdesk")
            );

            itCategories.forEach(categoryService::createNewCategory);

            // Создаем вакансии (по 3 на каждую категорию, всего 30)
            createItVacancies();
        }
    }

    private void createItVacancies() {
        // Вакансии в разработке
        createVacancy("Разработка", "Java-разработчик (Middle/Senior)",
                "Разработка backend-приложений на Spring Framework", 180000);
        createVacancy("Разработка", "Frontend-разработчик (React)",
                "Разработка SPA-приложений на React.js", 150000);
        createVacancy("Разработка", "Fullstack разработчик (Node.js + React)",
                "Полный цикл разработки веб-приложений", 200000);

        // Вакансии в тестировании
        createVacancy("Тестирование", "QA Engineer (Manual)",
                "Ручное тестирование веб и мобильных приложений", 90000);
        createVacancy("Тестирование", "Automation QA (Java)",
                "Автоматизация тестирования на Selenium", 140000);
        createVacancy("Тестирование", "QA Lead",
                "Руководство командой тестировщиков", 170000);

        // DevOps вакансии
        createVacancy("DevOps", "DevOps Engineer",
                "Настройка CI/CD, работа с Kubernetes", 220000);
        createVacancy("DevOps", "System Administrator",
                "Администрирование Linux-серверов", 120000);
        createVacancy("DevOps", "Cloud Engineer (AWS)",
                "Развертывание и поддержка облачной инфраструктуры", 190000);

        // Data Science вакансии
        createVacancy("Data Science", "Data Analyst",
                "Анализ данных, построение отчетов", 150000);
        createVacancy("Data Science", "Machine Learning Engineer",
                "Разработка ML-моделей", 210000);
        createVacancy("Data Science", "Data Engineer",
                "Построение ETL-процессов", 180000);

        // Вакансии в кибербезопасности
        createVacancy("Кибербезопасность", "Security Analyst",
                "Мониторинг и анализ угроз", 160000);
        createVacancy("Кибербезопасность", "Pentester",
                "Тестирование на проникновение", 190000);
        createVacancy("Кибербезопасность", "CISO",
                "Руководство ИБ-направлением", 250000);

        // Вакансии в управлении проектами
        createVacancy("Управление проектами", "Project Manager",
                "Управление IT-проектами", 170000);
        createVacancy("Управление проектами", "Scrum Master",
                "Фасилитация agile-процессов", 150000);
        createVacancy("Управление проектами", "Product Owner",
                "Управление продуктом", 200000);

        // Вакансии в аналитике
        createVacancy("Аналитика", "Бизнес-аналитик",
                "Анализ требований, написание ТЗ", 130000);
        createVacancy("Аналитика", "Системный аналитик",
                "Проектирование систем", 150000);
        createVacancy("Аналитика", "BI-разработчик",
                "Разработка отчетов в Power BI", 160000);

        // Вакансии в дизайне
        createVacancy("Дизайн", "UI/UX Designer",
                "Проектирование интерфейсов", 120000);
        createVacancy("Дизайн", "Graphic Designer",
                "Создание графических материалов", 90000);
        createVacancy("Дизайн", "Product Designer",
                "Полный цикл дизайна продукта", 150000);

        // Вакансии в администрировании
        createVacancy("Администрирование", "Linux Administrator",
                "Администрирование серверов", 110000);
        createVacancy("Администрирование", "Network Engineer",
                "Настройка сетевого оборудования", 140000);
        createVacancy("Администрирование", "Database Administrator",
                "Администрирование СУБД", 150000);

        // Вакансии в техподдержке
        createVacancy("Техподдержка", "IT-специалист",
                "Поддержка пользователей", 60000);
        createVacancy("Техподдержка", "Helpdesk Engineer",
                "Решение инцидентов 1-2 линии", 80000);
        createVacancy("Техподдержка", "IT-менеджер",
                "Организация работы IT-инфраструктуры", 120000);
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