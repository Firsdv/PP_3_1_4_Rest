package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dto.UserResponseDto;
import ru.kata.spring.boot_security.demo.entityes.Role;
import ru.kata.spring.boot_security.demo.entityes.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;
import ru.kata.spring.boot_security.demo.dto.UserDto;

import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           RoleService roleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    @Override
    @Transactional
    public List<User> findAll() {
        return userRepository.findAllWithRoles();
    }

    @Override
    @Transactional
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }


    @Override
    @Transactional
    public void save(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("Email already exists: " + user.getEmail());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }


    @Override
    @Transactional
    public void update(User user) {
        User existingUser = findById(user.getId());
        if (existingUser != null) {
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                user.setPassword(existingUser.getPassword());
            } else {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            userRepository.save(user);
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);

    }

    @Override
    @Transactional
    public User findByPrincipal(Principal principal) {
        return findByEmail(principal.getName());
    }

    @Override
    @Transactional
    public User findByAuthentication(Authentication auth) {
        return findByEmail(auth.getName());
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User  not found with email: " + email);
        }
        return user;
    }

    @Override
    @Transactional
    public void createFromDto(UserDto dto) {
        if (userRepository.findByEmail(dto.getEmail()) != null) {
            throw new IllegalArgumentException("Email already exists: " + dto.getEmail());
        }

        User user = new User();
        user.setFirstname(dto.getFirstname());
        user.setLastname(dto.getLastname());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        Set<Role> roles = dto.getRoleIds().stream()
                .map(roleService::findById)
                .filter(role -> role != null)
                .collect(Collectors.toSet());
        user.setRoles(roles);

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateFromDto(UserDto dto) {
        User existingUser = findById(dto.getId());
        if (existingUser == null) return;

        existingUser.setFirstname(dto.getFirstname());
        existingUser.setLastname(dto.getLastname());
        existingUser.setEmail(dto.getEmail());

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        if (dto.getRoleIds() != null) {
            Set<Role> roles = dto.getRoleIds().stream()
                    .map(roleService::findById)
                    .filter(role -> role != null)
                    .collect(Collectors.toSet());
            existingUser.setRoles(roles);
        }

        userRepository.save(existingUser);
    }

    public UserResponseDto toResponseDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setFirstname(user.getFirstname());
        dto.setLastname(user.getLastname());
        dto.setEmail(user.getEmail());
        dto.setRoles(user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet()));
        return dto;
    }
}
