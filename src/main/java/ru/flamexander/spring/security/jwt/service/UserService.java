package ru.flamexander.spring.security.jwt.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.flamexander.spring.security.jwt.dtos.RegistrationUserDto;
import ru.flamexander.spring.security.jwt.dtos.UserDto;
import ru.flamexander.spring.security.jwt.dtos.UserUpdateDto;
import ru.flamexander.spring.security.jwt.entities.FavoriteVacancy;
import ru.flamexander.spring.security.jwt.entities.PasswordResetToken;
import ru.flamexander.spring.security.jwt.entities.User;
import ru.flamexander.spring.security.jwt.entities.Vacancy;
import ru.flamexander.spring.security.jwt.exceptions.InvalidTokenException;
import ru.flamexander.spring.security.jwt.exceptions.TokenExpiredException;
import ru.flamexander.spring.security.jwt.exceptions.UserNotFoundException;
import ru.flamexander.spring.security.jwt.repositories.ApplicationsRepository;
import ru.flamexander.spring.security.jwt.repositories.FavoriteVacancyRepository;
import ru.flamexander.spring.security.jwt.repositories.PasswordResetTokenRepository;
import ru.flamexander.spring.security.jwt.repositories.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class UserService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private UserRepository userRepository;
    private RoleService roleService;
    private FavoriteVacancyService favoriteVacancyService;
    private static final String RESUME_UPLOAD_DIR = "uploads/resumes/";

    @Autowired
    private ApplicationsRepository applicationsRepository;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private FavoriteVacancyRepository favoriteVacancyRepository;

    public Object createUser(User user) {
        logger.info("Создание нового пользователя: {}", user.getUsername());
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        return null;
    }

    public String saveResume(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(RESUME_UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return RESUME_UPLOAD_DIR + fileName;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findByUsername(String username) {
        logger.debug("Поиск пользователя по логину: {}", username);
        return userRepository.findByUsername(username);
    }

    public Optional<User> findById(Long id) {
        logger.debug("Поиск пользователя по ID: {}", id);
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        logger.debug("Поиск пользователя по email: {}", email);
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Загрузка пользователя: {}", username);
        User user = findByUsername(username).orElseThrow(() -> {
            logger.error("Пользователь '{}' не найден", username);
            return new UsernameNotFoundException("Пользователь '%s' не найден".formatted(username));
        });

        List<SimpleGrantedAuthority> authorities = user.getUserRoles().stream()
                .map(userRole -> new SimpleGrantedAuthority(userRole.getRole().getName()))
                .collect(Collectors.toList());

        logger.debug("Успешная загрузка пользователя: {} (роли: {})", username, authorities);
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }

    public User createNewUser(RegistrationUserDto registrationUserDto) {
        logger.info("Регистрация нового пользователя: {}", registrationUserDto.getUsername());
        User user = new User();
        user.setUsername(registrationUserDto.getUsername());
        user.setEmail(registrationUserDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationUserDto.getPassword()));

        logger.debug("Назначение роли ROLE_USER новому пользователю");
        return userRoleService.createUserWithRole(user, roleService.getRoleIdByName("ROLE_USER"));
    }

    public boolean deleteById(Long id) {
        logger.info("Удаление пользователя с ID: {}", id);
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            logger.info("Пользователь с ID {} успешно удален", id);
            return true;
        }
        logger.warn("Попытка удаления несуществующего пользователя с ID: {}", id);
        return false;
    }

    public User updateUser(Long id, User userDetails) {
        logger.info("Обновление пользователя с ID: {}", id);
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User userToUpdate = optionalUser.get();
            logger.debug("Обновление данных пользователя: {}", userDetails);
            userToUpdate.setUsername(userDetails.getUsername());
            userToUpdate.setEmail(userDetails.getEmail());
            return userRepository.save(userToUpdate);
        }
        logger.error("Пользователь с ID {} не найден для обновления", id);
        return null;
    }

    public Object save(User user) {
        logger.info("Сохранение пользователя: {}", user.getUsername());
        if (user.getId() != null) {
            return updateUser(user.getId(), user);
        } else {
            return createUser(user);
        }
    }

    public User getCurrentUser() {
        logger.debug("Получение текущего пользователя");
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = principal instanceof UserDetails
                ? ((UserDetails) principal).getUsername()
                : principal.toString();

        logger.debug("Текущий пользователь: {}", username);
        return findByUsername(username).orElseThrow(() -> {
            logger.error("Текущий пользователь {} не найден", username);
            return new RuntimeException("Текущий пользователь не найден");
        });
    }

    public boolean existsByUsername(String username) {
        logger.debug("Проверка существования пользователя: {}", username);
        return findByUsername(username).isPresent();
    }

    public void addVacancyToFavorites(User user, Vacancy vacancy) {
        logger.info("Добавление вакансии {} в избранное пользователя {}", vacancy.getId(), user.getId());
        if (favoriteVacancyRepository.findByUserAndVacancy(user, vacancy).isEmpty()) {
            FavoriteVacancy favoriteVacancy = new FavoriteVacancy();
            favoriteVacancy.setUser(user);
            favoriteVacancy.setVacancy(vacancy);
            favoriteVacancyRepository.save(favoriteVacancy);
            logger.debug("Вакансия {} добавлена в избранное пользователя {}", vacancy.getId(), user.getId());
        } else {
            logger.warn("Вакансия {} уже в избранном пользователя {}", vacancy.getId(), user.getId());
        }
    }

    public List<Vacancy> getFavoriteVacancies(User user) {
        logger.debug("Получение избранных вакансий для пользователя {}", user.getId());
        return favoriteVacancyRepository.findByUser(user).stream()
                .map(FavoriteVacancy::getVacancy)
                .collect(Collectors.toList());
    }

    public void removeVacancyFromFavorites(User user, Vacancy vacancy) {
        logger.info("Удаление вакансии {} из избранного пользователя {}", vacancy.getId(), user.getId());
        Optional<FavoriteVacancy> favoriteVacancy = favoriteVacancyRepository.findByUserAndVacancy(user, vacancy);
        if (favoriteVacancy.isPresent()) {
            favoriteVacancyRepository.deleteByUserAndVacancy(user, vacancy);
            logger.debug("Вакансия {} удалена из избранного пользователя {}", vacancy.getId(), user.getId());
        } else {
            logger.warn("Вакансия {} не найдена в избранном пользователя {}", vacancy.getId(), user.getId());
        }
    }

    public boolean isVacancyInFavorites(User user, Vacancy vacancy) {
        logger.debug("Проверка вакансии {} в избранном пользователя {}", vacancy.getId(), user.getId());
        return favoriteVacancyRepository.findByUserAndVacancy(user, vacancy).isPresent();
    }

    public List<UserDto> getAllUsers() {
        logger.info("Получение списка всех пользователей");
        return StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .map(this::convertToUserDto)
                .collect(Collectors.toList());
    }

    public Page<UserDto> getAllUsers(Pageable pageable) {
        logger.info("Получение страницы пользователей: {}", pageable);
        return userRepository.findAll(pageable)
                .map(this::convertToUserDto);
    }

    private UserDto convertToUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getUserRoles().stream()
                        .map(ur -> ur.getRole().getName())
                        .collect(Collectors.toList())
        );
    }

    public User updateUser(Long id, UserUpdateDto userUpdateDto) {
        logger.info("Обновление пользователя с ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Пользователь с ID {} не найден", id);
                    return new ResourceNotFoundException("User not found");
                });

        if (!applicationsRepository.existsByUser(user)) {
            logger.debug("Обновление email пользователя {}", id);
            user.setEmail(userUpdateDto.getEmail());
        } else {
            logger.warn("Попытка изменить email пользователя {} с активными заявками", id);
            throw new RuntimeException("Невозможно изменить email при наличии активных заявок");
        }

        logger.debug("Обновление основных данных пользователя: {}", userUpdateDto);
        user.setUsername(userUpdateDto.getUsername());
        user.setPhone(userUpdateDto.getPhone());
        user.setDescription(userUpdateDto.getDescription());
        user.setSkills(userUpdateDto.getSkills());

        processAvatarUpload(user, userUpdateDto);
        processResumeUpload(user, userUpdateDto);

        if (userUpdateDto.getResumeFile() != null && !userUpdateDto.getResumeFile().isEmpty()) {
            try {
                String resumeFilename = saveResume(userUpdateDto.getResumeFile());
                user.setResumeFilename(resumeFilename);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save resume", e);
            }
        }

        return userRepository.save(user);
    }



    private void processAvatarUpload(User user, UserUpdateDto dto) {
        if (dto.getAvatarFile() != null && !dto.getAvatarFile().isEmpty()) {
            logger.info("Загрузка аватарки для пользователя {}", user.getId());
            String avatarFilename = storeFile(dto.getAvatarFile(), "avatars");
            user.setAvatarFilename("/uploads/avatars/" + avatarFilename);
        }
    }

    private void processResumeUpload(User user, UserUpdateDto dto) {
        if (dto.getResumeFile() != null && !dto.getResumeFile().isEmpty()) {
            logger.info("Загрузка резюме для пользователя {}", user.getId());
            String resumeFilename = storeFile(dto.getResumeFile(), "resumes");
            user.setResumeFilename("/uploads/resumes/" + resumeFilename);
        }
    }

    private String storeFile(MultipartFile file, String subdirectory) {
        try {
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            logger.debug("Сохранение файла: {}", originalFilename);

            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID() + fileExtension;
            Path uploadPath = Paths.get("uploads/" + subdirectory);

            if (!Files.exists(uploadPath)) {
                logger.debug("Создание директории: {}", uploadPath);
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            logger.info("Файл {} сохранен как {}", originalFilename, fileName);
            return fileName;
        } catch (IOException ex) {
            logger.error("Ошибка сохранения файла: {}", file.getOriginalFilename(), ex);
            throw new RuntimeException("Ошибка сохранения файла", ex);
        }
    }




}