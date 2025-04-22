package ru.kata.spring.boot_security.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kata.spring.boot_security.demo.entityes.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;

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
//    @GetMapping
//    public String showUsers(Model model, Principal principal) {
//        if (principal == null) {
//            return "redirect:/login";
//        }
//        User user = userRepository.findByEmail(principal.getName()); // Using Email
//        model.addAttribute("user", user);
//        model.addAttribute("firstname", user.getFirstname());
//        model.addAttribute("lastname", user.getLastname());
//        return "user";
//    }
//
//    @GetMapping("/user")
//    public String userPage(Model model) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        User user = userService.findByEmail(auth.getName());  //Добавил
//        model.addAttribute("user", user);  //Добавил
//        return "user"; // Имя вашего Thymeleaf шаблона (user.html)
//    }
