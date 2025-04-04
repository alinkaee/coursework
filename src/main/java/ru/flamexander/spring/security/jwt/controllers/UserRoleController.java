package ru.flamexander.spring.security.jwt.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.flamexander.spring.security.jwt.dtos.UserRoleDTO;
import ru.flamexander.spring.security.jwt.entities.UserRole;
import ru.flamexander.spring.security.jwt.service.ResourceNotFoundException;
import ru.flamexander.spring.security.jwt.service.UserRoleService;

import java.util.List;

@RestController
@RequestMapping("/api/user-roles")
@RequiredArgsConstructor
public class UserRoleController {

    private final UserRoleService userRoleService; // Предполагается, что у вас есть сервисный слой

    @PostMapping
    public ResponseEntity<UserRoleDTO> assignRoleToUser(@RequestBody UserRoleDTO userRoleDTO) {
        return ResponseEntity.ok(userRoleService.assignRole(userRoleDTO));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserRoleDTO>> getRolesByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(userRoleService.findByUserId(userId));
    }

    @GetMapping("/role/{roleId}")
    public ResponseEntity<List<UserRoleDTO>> getUsersByRoleId(@PathVariable Integer roleId) {
        return ResponseEntity.ok(userRoleService.findByRoleId(roleId));
    }

    @DeleteMapping
    public ResponseEntity<Void> removeRoleFromUser(@RequestBody UserRoleDTO userRoleDTO) {
        userRoleService.removeRole(userRoleDTO);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/{id}")
    public ResponseEntity<UserRoleDTO> updateUserRole(@PathVariable Long id, @RequestBody UserRoleDTO userRoleDTO) {
        return ResponseEntity.ok(userRoleService.updateUserRole(id, userRoleDTO));


    }
}