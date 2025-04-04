package ru.flamexander.spring.security.jwt.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.flamexander.spring.security.jwt.entities.UserRole;

import java.util.List;
import java.util.Optional;

// src/main/java/ru/flamexander/spring/security/jwt/repositories/UserRoleRepository.java
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    // Проверка существования связи пользователь-роль
    boolean existsByUserIdAndRoleId(Long userId, Integer roleId);

    // Поиск всех ролей пользователя
    List<UserRole> findByUserId(Long userId);

    // Поиск всех пользователей с определенной ролью
    List<UserRole> findByRoleId(Integer roleId);

    // Поиск конкретной связи пользователь-роль
    Optional<UserRole> findByUserIdAndRoleId(Long userId, Integer roleId);
}