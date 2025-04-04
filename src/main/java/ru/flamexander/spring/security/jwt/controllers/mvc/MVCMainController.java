package ru.flamexander.spring.security.jwt.controllers.mvc;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MVCMainController {
    @GetMapping("/")
    public String register() {
        return "register";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/news")
    public String showNewsPage() {
        return "news";
    }

    @GetMapping("/contact")
    public String showContactPage() {
        return "contact";
    }




}
