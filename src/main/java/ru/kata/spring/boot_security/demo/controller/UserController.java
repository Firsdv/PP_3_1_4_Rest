package ru.kata.spring.boot_security.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kata.spring.boot_security.demo.entityes.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;
import java.util.Optional;


@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String showUserPage(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login"; // Перенаправлять на страницу логина, если пользователь не аутентифицирован
        }

        String email = principal.getName();
        User user = userRepository.findByEmail(email);

        if (user == null) {
            // Обработка ситуации, когда пользователь с таким email не найден.
            // Можно выбросить исключение или перенаправить на страницу ошибки.
            return "error";  // Или какое-то другое представление для обработки ошибки
        }

        model.addAttribute("user", user);  // Передаем объект user в модель

        return "user"; // Имя вашего Thymeleaf шаблона (user.html)
    }
}
