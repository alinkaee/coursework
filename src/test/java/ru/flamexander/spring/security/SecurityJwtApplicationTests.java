package ru.flamexander.spring.security;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import ru.flamexander.spring.security.jwt.controllers.AuthController;
import ru.flamexander.spring.security.jwt.dtos.CategoriesDTO;
import ru.flamexander.spring.security.jwt.dtos.RegistrationUserDto;
import ru.flamexander.spring.security.jwt.entities.*;
import ru.flamexander.spring.security.jwt.exceptions.AppError;
import ru.flamexander.spring.security.jwt.exceptions.GlobalExceptionHandler;
import ru.flamexander.spring.security.jwt.exceptions.UserNotFoundException;
import ru.flamexander.spring.security.jwt.repositories.*;
import ru.flamexander.spring.security.jwt.service.*;
import ru.flamexander.spring.security.jwt.utils.JwtTokenUtils;
import ru.flamexander.spring.security.jwt.utils.MailUtils;

import java.time.Duration;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// === Основной тестовый класс с разделением на модульные и интеграционные тесты ===
@SpringBootTest(classes = ru.flamexander.spring.security.jwt.SecurityJwtApplication.class)
@AutoConfigureMockMvc
class SecurityJwtApplicationTests {

	// === МОДУЛЬНЫЕ ТЕСТЫ (без использования Spring контекста, используют моки) ===
	@Nested
	@ExtendWith(MockitoExtension.class)
	class UnitTests {
		@Mock
		private UserService userService;
		@Mock
		private AuthService authService;
		@Mock
		private RoleRepository roleRepository;
		@InjectMocks
		private AuthController authController;
		@InjectMocks
		private RoleService roleService;
		@Mock
		private CategoryRepository categoryRepository;
		@InjectMocks
		private CategoryService categoryService;
		@Mock
		private VacancyRepository vacancyRepository;
		@InjectMocks
		private VacancyService vacancyService;
		@Mock
		private ApplicationsRepository applicationsRepository;
		@InjectMocks
		private ApplicationsService applicationsService;
		@Mock
		private PasswordResetTokenRepository tokenRepository;
		@InjectMocks
		private PasswordResetService passwordResetService;
		@Mock
		private EmailService emailService;
		@InjectMocks
		private EmailNotificationService emailNotificationService;
		@InjectMocks
		private GlobalExceptionHandler globalExceptionHandler;


		@Test
		void testUserServiceLoadUser() {
			User mockUser = new User();
			mockUser.setUsername("dbuser");
			mockUser.setPassword("encodedpass");

			when(userService.findByUsername("dbuser")).thenReturn(Optional.of(mockUser));

			Optional<User> result = userService.findByUsername("dbuser");
			assertTrue(result.isPresent());
			assertEquals("dbuser", result.get().getUsername());
		}

		@Test
		void testCategoryServiceCreateCategory() {
			// 1. Создаем DTO
			CategoriesDTO dto = new CategoriesDTO();
			dto.setTitle("Java");
			dto.setDescription("Java Development");

			// 2. Мокаем сохранение в репозитории
			Category mockCategory = new Category();
			mockCategory.setId(1L);
			mockCategory.setTitle("Java");
			mockCategory.setDescription("Java Development");

			when(categoryRepository.save(any(Category.class))).thenReturn(mockCategory);

			// 3. Вызываем сервис (предположим, что он принимает DTO)
			Category result = categoryService.createNewCategory(dto);

			// 4. Проверяем результат
			assertNotNull(result);
			assertEquals("Java", result.getTitle());
			assertEquals("Java Development", result.getDescription());
			verify(categoryRepository, times(1)).save(any(Category.class));
		}

		@Test
		void testCategoryDtoValidation() {
			CategoriesDTO dto = new CategoriesDTO();
			dto.setId(1L);
			dto.setTitle("Java");

			assertEquals(1L, dto.getId());
			assertEquals("Java", dto.getTitle());
		}

		@Test
		void testEmailNotificationServiceSendEmail() {
			String to = "user@example.com";
			String subject = "Test Subject";
			String text = "Test message content";

			emailNotificationService.sendStatusChangeNotification("user@example.com", "subject", "text", "kkkkkk");
		}

//		@Test
//		void testResourceNotFoundException() {
//			ResourceNotFoundException ex = new ResourceNotFoundException("Category");
//
//			assertNotNull(ex.getMessage());
//			assertTrue(ex.getMessage().contains("Category with id=1 was not found"));
//		}

//		@Test
//		void testGlobalExceptionHandlerUserNotFoundException() {
//			UserNotFoundException ex = new UserNotFoundException("User not found");
//			AppError error = globalExceptionHandler.handleUserNotFoundException(ex);
//
//			assertNotNull(error);
//			assertEquals(HttpStatus.NOT_FOUND.value(), error.getStatus());
//			assertTrue(error.getMessage().contains("User not found"));
//		}

//		@Test
//		void testMailUtilsBuildMailContent() {
//			String username = "testuser";
//			String token = "reset-token-123";
//			String expectedLink = "http://example.com/reset?token=" + token;
//
//			String mailContent = MailUtils.buildResetPasswordMail(username, expectedLink);
//
//			assertThat(mailContent).contains("testuser");
//			assertThat(mailContent).contains(expectedLink);
//			assertThat(mailContent).contains("password reset");
//		}




//		@Test
//		void testApplicationsServiceCreateApplication() {
//			Applications mockApp = new Applications();
//			mockApp.setId(1L);
//			mockApp.setStatus(Applications.ApplicationStatus.PENDING);
//
//			when(applicationsRepository.save(any(Applications.class))).thenReturn(mockApp);
//
//			Applications result = applicationsService.createApplication(mockApp);
//			assertNotNull(result);
//			assertEquals(Applications.ApplicationStatus.PENDING, result.getStatus());
//			verify(applicationsRepository, times(1)).save(any(Applications.class));
//		}



//		@Test
//		void testVacancyServiceCreateVacancy() {
//			Vacancy mockVacancy = new Vacancy();
//			mockVacancy.setId(1L);
//			mockVacancy.setTitle("Java Developer");
//
//			when(vacancyRepository.save(any(Vacancy.class))).thenReturn(mockVacancy);
//
//			Vacancy result = vacancyService.createVacancy(mockVacancy);
//			assertNotNull(result);
//			assertEquals("Java Developer", result.getTitle());
//			verify(vacancyRepository, times(1)).save(any(Vacancy.class));
//		}




	}

	// === ИНТЕГРАЦИОННЫЕ ТЕСТЫ (работают с реальным Spring контекстом и базой данных) ===
	@Nested
	@SpringBootTest(classes = ru.flamexander.spring.security.jwt.SecurityJwtApplication.class)
	class IntegrationTests {
		@Autowired
		private MockMvc mockMvc;

		@Autowired
		private UserRepository userRepository;

		@Autowired
		private UserService userService;

		@Test
		void contextLoads() {
			assertNotNull(mockMvc);
			assertNotNull(userService);
		}

		@Test
		void testRegistrationFormSubmission() throws Exception {
			mockMvc.perform(post("/reg")
							.param("name", "integuser")
							.param("password", "IntegPass123!")
							.param("confirmPassword", "IntegPass123!")
							.param("email", "integ@example.com")
							.contentType(MediaType.APPLICATION_FORM_URLENCODED))
					.andExpect(status().is3xxRedirection())
					.andExpect(redirectedUrl("/login"));
		}

		@Test
		void testUserRepositoryOperations() {
			User user = new User();
			user.setUsername("repo_test");
			user.setPassword("RepoPass123!");
			user.setEmail("repo@test.com");

			User saved = userRepository.save(user);
			assertNotNull(saved.getId());

			Optional<User> found = userRepository.findByUsername("repo_test");
			assertTrue(found.isPresent());
			assertEquals("repo@test.com", found.get().getEmail());

			userRepository.deleteById(saved.getId());
			assertFalse(userRepository.findByUsername("repo_test").isPresent());
		}

		@Test
		void testUserServiceFindById() {
			User user = new User();
			user.setUsername("service_test");
			user.setPassword("ServicePass123!");
			user.setEmail("service@test.com");
			User saved = userRepository.save(user);

			Optional<User> result = userService.findById(saved.getId());
			assertTrue(result.isPresent());
			assertEquals("service@test.com", result.get().getEmail());

			userRepository.deleteById(saved.getId());
		}

		@Test
		void testUserEntityValidation() {
			User user = new User();

			user.setUsername("entity_test");
			user.setEmail("entity@test.com");
			user.setPhone("+1234567890");
			user.setSkills("Java, Spring");
			user.setDescription("Test description");

			assertEquals("entity_test", user.getUsername());
			assertEquals("entity@test.com", user.getEmail());
			assertEquals("+1234567890", user.getPhone());
			assertEquals("Java, Spring", user.getSkills());
			assertEquals("Test description", user.getDescription());
		}

		@Test
		void testRegistrationDtoValidation() {
			RegistrationUserDto dto = new RegistrationUserDto();
			dto.setUsername("dto_user");
			dto.setPassword("DtoPass123!");
			dto.setConfirmPassword("DtoPass123!");
			dto.setEmail("dto@test.com");

			assertEquals("dto_user", dto.getUsername());
			assertEquals("DtoPass123!", dto.getPassword());
			assertEquals("DtoPass123!", dto.getConfirmPassword());
			assertEquals("dto@test.com", dto.getEmail());
		}
	}
}