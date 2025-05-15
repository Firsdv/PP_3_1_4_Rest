package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.Authentication;
import ru.kata.spring.boot_security.demo.dto.UserDto;
import ru.kata.spring.boot_security.demo.dto.UserResponseDto;
import ru.kata.spring.boot_security.demo.entityes.User;

import java.security.Principal;
import java.util.List;

public interface UserService {

    void save(User user);

    User findById(Long userId);

    List<User> findAll();

    void update(User user);

    void delete(Long userId);

    User findByEmail(String email);

    User findByPrincipal(Principal principal);

    User findByAuthentication(Authentication auth);

    void createFromDto(UserDto userDto);

    void updateFromDto(UserDto userDto);

    UserResponseDto toResponseDto(User user);
}