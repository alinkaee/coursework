package ru.flamexander.spring.security.jwt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.flamexander.spring.security.jwt.dtos.UserRoleDTO;
import ru.flamexander.spring.security.jwt.entities.Role;
import ru.flamexander.spring.security.jwt.entities.User;
import ru.flamexander.spring.security.jwt.entities.UserRole;
import ru.flamexander.spring.security.jwt.repositories.RoleRepository;
import ru.flamexander.spring.security.jwt.repositories.UserRepository;
import ru.flamexander.spring.security.jwt.repositories.UserRoleRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserRoleDTO assignRole(UserRoleDTO userRoleDTO) {
        // Проверяем существование пользователя и роли
        User user = userRepository.findById(userRoleDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Role role = roleRepository.findById(userRoleDTO.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        // Проверяем, не существует ли уже такая связь
        if (userRoleRepository.existsByUserIdAndRoleId(
                userRoleDTO.getUserId(),
                userRoleDTO.getRoleId())) {
            throw new IllegalStateException("This role is already assigned to the user");
        }

        // Создаем новую связь
        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        userRoleRepository.save(userRole);

        return userRoleDTO;
    }

    public User createUserWithRole(User user, Integer roleId) {
        // Сохраняем пользователя
        User savedUser = userRepository.save(user);

        // Получаем роль
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        // Создаем новую связь между пользователем и ролью
        UserRole userRole = new UserRole();
        userRole.setUser(savedUser);
        userRole.setRole(role);
        userRoleRepository.save(userRole);

        return savedUser;
    }


    public List<UserRoleDTO> findByUserId(Long userId) {
        // Проверяем существование пользователя
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }

        return userRoleRepository.findByUserId(userId)
                .stream()
                .map(userRole -> new UserRoleDTO(
                        userRole.getUser().getId(),
                        userRole.getRole().getId()))
                .collect(Collectors.toList());
    }

    public List<UserRoleDTO> findByRoleId(Integer roleId) {
        // Проверяем существование роли
        if (!roleRepository.existsById(roleId)) {
            throw new ResourceNotFoundException("Role not found");
        }

        return userRoleRepository.findByRoleId(roleId)
                .stream()
                .map(userRole -> new UserRoleDTO(
                        userRole.getUser().getId(),
                        userRole.getRole().getId()))
                .collect(Collectors.toList());
    }

    public void removeRole(UserRoleDTO userRoleDTO) {
        UserRole userRole = userRoleRepository
                .findByUserIdAndRoleId(userRoleDTO.getUserId(), userRoleDTO.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("User-Role association not found"));

        userRoleRepository.delete(userRole);
    }
    // src/main/java/ru/flamexander/spring/security/jwt/services/UserRoleService.java
    public UserRoleDTO updateUserRole(Long id, UserRoleDTO userRoleDTO) {
        UserRole userRole = userRoleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserRole not found with id: " + id));

        User user = userRepository.findById(userRoleDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Role role = roleRepository.findById(userRoleDTO.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        userRole.setUser(user);
        userRole.setRole(role);
        userRoleRepository.save(userRole);

        return userRoleDTO;
    }

    public void saveUserRole(UserRole userRole) {
        userRoleRepository.save(userRole);
    }
}
