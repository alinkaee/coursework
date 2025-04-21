package ru.flamexander.spring.security.jwt.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.flamexander.spring.security.jwt.dtos.UserUpdateDto;
import ru.flamexander.spring.security.jwt.entities.User;
import ru.flamexander.spring.security.jwt.service.ResourceNotFoundException;
import ru.flamexander.spring.security.jwt.service.UserService;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        User user = userService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        model.addAttribute("user", user);
        model.addAttribute("userUpdateDto", new UserUpdateDto());
        return "profile-editing";
    }

    @PostMapping("/update/{id}")
    public String updateUser(
            @PathVariable Long id,
            @ModelAttribute UserUpdateDto userUpdateDto,
            @RequestParam("resume") MultipartFile resumeFile,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        try {
            // Получаем текущего пользователя
            User currentUser = userService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

            // Проверяем, изменилось ли имя пользователя
            boolean isUsernameChanged = !currentUser.getUsername().equals(userUpdateDto.getUsername());

            // Проверяем, был ли загружен файл
            if (userUpdateDto.getResumeFile() != null && !userUpdateDto.getResumeFile().isEmpty()) {
                // Сохраняем файл и получаем путь к нему
                String resumeFilename = userService.saveResume(userUpdateDto.getResumeFile());
                userUpdateDto.setResumeFilename(resumeFilename); // Устанавливаем имя файла в DTO
            }

            // Обновляем данные пользователя
            User updatedUser = userService.updateUser(id, userUpdateDto);

            // Добавляем сообщение об успешном обновлении
            redirectAttributes.addFlashAttribute("success", "Профиль успешно обновлен");

            // Если имя пользователя изменилось, очищаем сессию и перенаправляем на страницу входа
            if (isUsernameChanged) {
                session.invalidate(); // Очищаем сессию
                return "redirect:/login?usernameChanged=true";
            }

            // Иначе возвращаемся на страницу профиля
            return "redirect:/profile";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при обновлении профиля: " + e.getMessage());
            return "redirect:/profile-editing";
        }
    }

//    @PutMapping("/update/{id}")
//    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
//        User updatedUser = userService.updateUser(id, userDetails);
//        return ResponseEntity.ok(updatedUser);
//    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(user -> ResponseEntity.ok(user))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        boolean isDeleted = userService.deleteById(id);
        if (isDeleted) {
            return ResponseEntity.noContent().build(); // Возвращаем 204 No Content, если удаление прошло успешно
        } else {
            return ResponseEntity.notFound().build(); // Возвращаем 404 Not Found, если пользователь не найден
        }
    }

}
