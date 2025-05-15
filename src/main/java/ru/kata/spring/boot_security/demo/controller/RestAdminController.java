package ru.kata.spring.boot_security.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.dto.UserDto;
import ru.kata.spring.boot_security.demo.dto.UserResponseDto;
import ru.kata.spring.boot_security.demo.entityes.Role;
import ru.kata.spring.boot_security.demo.entityes.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class RestAdminController {

    private final UserService userService;
    private final RoleService roleService;

    public RestAdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/users")
    public List<UserResponseDto> getAllUsers() {
        return userService.findAll().stream()
                .map(userService::toResponseDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        User user = userService.findById(id);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @PostMapping("/users")
    public ResponseEntity<Void> createUser(@RequestBody UserDto userDto) {
        userService.createFromDto(userDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/users/update/{id}")
    public ResponseEntity<Void> updateUserWorkaround(@PathVariable Long id, @RequestBody UserDto userDto) {
        userDto.setId(id);
        userService.updateFromDto(userDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/users/delete/{id}")
    public ResponseEntity<Void> deleteUserWorkaround(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/roles")
    public List<Role> getAllRoles() {
        return roleService.getRoles();
    }
}
