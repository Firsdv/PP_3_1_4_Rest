package ru.kata.spring.boot_security.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaticRedirectController {

    @GetMapping("/admin")
    public String redirectToAdmin() {
        return "redirect:/admin.html";
    }

    @GetMapping("/user")
    public String redirectToUser() {
        return "redirect:/user.html";
    }
}