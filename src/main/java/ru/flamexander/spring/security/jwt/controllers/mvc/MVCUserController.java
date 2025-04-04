package ru.flamexander.spring.security.jwt.controllers.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class MVCUserController {

    @GetMapping("/profile")
    public String getProfilePage() {
        return "user/profile";
    }

    @GetMapping("/add_application")
    public String showAddedApplication() {
        return "user/add-application";
    }

    @PostMapping("/add_application")
    public String AddApplication() {
        return "user/add-application";
    }

}
