package ru.flamexander.spring.security.jwt.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import ru.flamexander.spring.security.jwt.dtos.JwtResponse;
import ru.flamexander.spring.security.jwt.dtos.RegistrationUserDto;
import ru.flamexander.spring.security.jwt.exceptions.AppError;
import ru.flamexander.spring.security.jwt.service.AuthService;
import ru.flamexander.spring.security.jwt.service.UserService;
import ru.flamexander.spring.security.jwt.utils.JwtTokenUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    private final AuthService authService;

    private final UserService userService;


    @PostMapping("/reg")
    public ModelAndView createNewUser(
            @RequestParam String name,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            @RequestParam String email,
            RedirectAttributes redirectAttributes
    ) {
        RegistrationUserDto registrationUserDto = new RegistrationUserDto();
        registrationUserDto.setUsername(name);
        registrationUserDto.setPassword(password);
        registrationUserDto.setConfirmPassword(confirmPassword);
        registrationUserDto.setEmail(email);

        List<String> errors = new ArrayList<>();

        if (password.isEmpty() || confirmPassword.isEmpty() || name.isEmpty() || email.isEmpty()) {
            errors.add("Все поля должны быть заполнены.");
        }

        if (!password.equals(confirmPassword)) {
            errors.add("Пароли не совпадают.");
        }

        // Дополнительная валидация email
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            errors.add("Некорректный формат email.");
        }

        if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d{2,})(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
            errors.add("Пароль должен содержать минимум 8 символов, одну заглавную букву, одну строчную букву, две цифры и один специальный символ.");
        }

        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("errors", errors);
            return new ModelAndView(new RedirectView("/"));
        }

        authService.createNewUser(registrationUserDto);

        return new ModelAndView(new RedirectView("/login"));
    }



    @PostMapping("/login")
    public ModelAndView createAuthToken(
            @RequestParam String username,
            @RequestParam String password,
            HttpServletResponse response
    ) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
        } catch (BadCredentialsException e) {
            ModelAndView modelAndView = new ModelAndView("login");
            modelAndView.addObject("error", "Неправильный логин или пароль");
            return modelAndView;
        }

        UserDetails userDetails = userService.loadUserByUsername(username);
        String token = jwtTokenUtils.generateToken(userDetails);

        ResponseCookie cookie = jwtTokenUtils.createJwtCookie(token);
        response.addHeader("Set-Cookie", cookie.toString());

        return new ModelAndView(new RedirectView("/index"));
    }


    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        // Удаление куки или токена аутентификации
        Cookie cookie = new Cookie("auth-token", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        SecurityContextHolder.clearContext(); // Очистка контекста безопасности

        return "redirect:/index"; // Перенаправление на главную страницу
    }

}

// Класс для запроса входа
class AuthRequest {
    private String username;
    private String password;

    // Getters и Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    static class AuthResponse {
        private String token;

        public AuthResponse(String token) {
            this.token = token;
        }
    }

}
