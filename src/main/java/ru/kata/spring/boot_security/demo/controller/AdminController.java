package ru.kata.spring.boot_security.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.entityes.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;
    private final UserRepository userRepository;

    public AdminController(UserService userService, RoleService roleService, UserRepository userRepository) {
        this.userService = userService;
        this.roleService = roleService;
        this.userRepository = userRepository;
    }
    @GetMapping
    public String showUserList(
            @RequestParam(value = "editId", required = false) Long editId,
            @RequestParam(value = "deleteId", required = false) Long deleteId,
            Model model, Principal principal) {

        User authUser = userRepository.findByEmail(principal.getName());
        model.addAttribute("authUser", authUser);
        model.addAttribute("users", userService.findAll());
        model.addAttribute("allRoles", roleService.getRoles());

        if (editId != null) {
            User editUser = userService.findById(editId);
            model.addAttribute("editUser", editUser);
        }

        if (deleteId != null) {
            User deleteUser = userService.findById(deleteId);
            model.addAttribute("deleteUser", deleteUser);
        }

        return "admin";
    }

    @GetMapping("/new")
    public String createUser(Model model, Principal principal) {
        User authUser = userRepository.findByEmail(principal.getName());
        model.addAttribute("authUser", authUser);
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleService.getRoles());
        return "new";
    }

    @PostMapping("/new")
    public String createUser(@ModelAttribute("user") @Valid User user, BindingResult result) {
        if (result.hasErrors()) {
            return "new";
        }
        userService.save(user);
        return "redirect:/admin";
    }

    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable("id") Long id, Model model, Principal principal) {
        User authUser = userRepository.findByEmail(principal.getName());
        model.addAttribute("authUser", authUser);
        model.addAttribute("editUser", userService.findById(id));
        model.addAttribute("allRoles", roleService.getRoles());
        return "admin";
    }


    @PostMapping("/update")
    public String updateUser(@ModelAttribute("editUser") User user) {
        userService.update(user);
        return "redirect:/admin";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.delete(id);
        return "redirect:/admin";
    }
}