package ru.flamexander.spring.security.jwt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.flamexander.spring.security.jwt.entities.Role;
import ru.flamexander.spring.security.jwt.repositories.RoleRepository;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;


    public Optional<Role> getRoleByName(String roleName) {
        return roleRepository.findByName(roleName);
    }

    public Integer getRoleIdByName(String roleName) {
        return roleRepository.findByName(roleName)
                .map(Role::getId)
                .orElseThrow(() -> new RuntimeException("Роль не найдена"));
    }

    // Добавьте этот метод
    public void deleteAll() {
        roleRepository.deleteAll();
    }

}
