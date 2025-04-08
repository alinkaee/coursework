package ru.flamexander.spring.security.jwt.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.flamexander.spring.security.jwt.dtos.RegistrationUserDto;
import ru.flamexander.spring.security.jwt.entities.FavoriteVacancy;
import ru.flamexander.spring.security.jwt.entities.User;
import ru.flamexander.spring.security.jwt.entities.Vacancy;
import ru.flamexander.spring.security.jwt.repositories.FavoriteVacancyRepository;
import ru.flamexander.spring.security.jwt.repositories.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    private UserRepository userRepository;
    private RoleService roleService;

    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Object createUser(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        return null;
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
        return userRepository.findByUsername(username);
    }
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(
                String.format("Пользователь '%s' не найден", username)
        ));

        // Изменение для работы с UserRole
        List<SimpleGrantedAuthority> authorities = user.getUserRoles().stream()
                .map(userRole -> new SimpleGrantedAuthority(userRole.getRole().getName()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }

    public User createNewUser(RegistrationUserDto registrationUserDto) {
        User user = new User();
        user.setUsername(registrationUserDto.getUsername());
        user.setEmail(registrationUserDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationUserDto.getPassword()));

        // Создаем нового пользователя с ролью
        return userRoleService.createUserWithRole(user, roleService.getRoleIdByName("ROLE_USER"));
    }



    public boolean deleteById(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true; // Удаление прошло успешно
        }
        return false; // Пользователь не найден
    }
    public User updateUser(Long id, User userDetails) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User userToUpdate = optionalUser.get();
            userToUpdate.setUsername(userDetails.getUsername());
            userToUpdate.setEmail(userDetails.getEmail());
            // Обновите другие поля по мере необходимости
            return userRepository.save(userToUpdate);
        }
        return null; // Или выбросьте исключение, если пользователь не найден
    }

    public Object save(User user) {
        if (user.getId() != null) {
            // Обновление существующего пользователя
            return updateUser(user.getId(), user);
        } else {
            // Создание нового пользователя
            return createUser(user);
        }
    }

    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        return findByUsername(username).orElseThrow(() -> new RuntimeException("Текущий пользователь не найден"));
    }

    public boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }

    @Autowired
    private FavoriteVacancyRepository favoriteVacancyRepository;

    public void addVacancyToFavorites(User user, Vacancy vacancy) {
        if (favoriteVacancyRepository.findByUserAndVacancy(user, vacancy).isEmpty()) {
            FavoriteVacancy favoriteVacancy = new FavoriteVacancy();
            favoriteVacancy.setUser(user);
            favoriteVacancy.setVacancy(vacancy);
            favoriteVacancyRepository.save(favoriteVacancy);
        }
    }

    public List<Vacancy> getFavoriteVacancies(User user) {
        return favoriteVacancyRepository.findByUser(user).stream()
                .map(FavoriteVacancy::getVacancy)
                .collect(Collectors.toList());
    }

    public void removeVacancyFromFavorites(User user, Vacancy vacancy) {
        Optional<FavoriteVacancy> favoriteVacancy = favoriteVacancyRepository.findByUserAndVacancy(user, vacancy);
        if (favoriteVacancy.isPresent()) {
            favoriteVacancyRepository.deleteByUserAndVacancy(user, vacancy);
        }
    }

    public boolean isVacancyInFavorites(User user, Vacancy vacancy) {
        return favoriteVacancyRepository.findByUserAndVacancy(user, vacancy).isPresent();
    }
}
