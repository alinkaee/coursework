package ru.flamexander.spring.security.jwt.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.flamexander.spring.security.jwt.dtos.UserDto;
import ru.flamexander.spring.security.jwt.entities.User;
import ru.flamexander.spring.security.jwt.service.ResourceNotFoundException;
import ru.flamexander.spring.security.jwt.service.UserService;

@Controller
@RequestMapping("/users")
public class AdminUserController {
    private final UserService userService;

    @Autowired
    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String getAllUsers(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false, defaultValue = "id,asc") String[] sort,
            Model model) {

        if (page == null || size == null) {
            model.addAttribute("users", userService.getAllUsers());
        } else {
            Sort.Direction direction = sort[1].equalsIgnoreCase("desc")
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;

            Page<UserDto> usersPage = userService.getAllUsers(
                    PageRequest.of(page, size, Sort.by(direction, sort[0]))
            );
            model.addAttribute("users", usersPage);
            model.addAttribute("currentPage", page); // Добавляем текущую страницу в модель
            model.addAttribute("totalPages", usersPage.getTotalPages()); // Добавляем общее количество страниц
        }

        return "user/view-all-users";
    }

    @GetMapping("/view/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        User user = userService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        model.addAttribute("user", user);
        return "user/user-view"; // имя шаблона для просмотра пользователя
    }
}
