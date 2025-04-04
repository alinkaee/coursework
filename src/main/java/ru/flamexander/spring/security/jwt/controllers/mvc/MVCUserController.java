package ru.flamexander.spring.security.jwt.controllers.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.flamexander.spring.security.jwt.entities.Category;

import java.util.List;
@Controller
@RequiredArgsConstructor
public class MVCUserController {

    @GetMapping("/profile")
    public String getProfilePage() {
        return "user/profile";
    }
}
