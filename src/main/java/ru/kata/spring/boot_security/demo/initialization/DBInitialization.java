package ru.kata.spring.boot_security.demo.initialization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.entityes.Role;
import ru.kata.spring.boot_security.demo.entityes.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class DBInitialization {

    private final UserService userService;
    private final RoleService roleService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(DBInitialization.class);

    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";

    @Autowired
    public DBInitialization(UserService userService, RoleService roleService,
                            PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.userService = userService;
        this.roleService = roleService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        Role adminRole = createRoleIfNotFound(ROLE_ADMIN);
        Role userRole = createRoleIfNotFound(ROLE_USER);

        // Создаем пользователя с несколькими ролями
        createUserIfNotFound("111", "admin@example.com", "AdminFirstName",
                "AdminLastName", Set.of(adminRole, userRole));
        createUserIfNotFound("222", "user@example.com", "User FirstName",
                "User LastName", Collections.singleton(userRole));
    }

    private Role createRoleIfNotFound(String roleName) {
        Role role = roleService.findByName(roleName);
        if (role == null) {
            role = new Role();
            role.setName(roleName);
            try {
                roleService.addRole(role);
                logger.info("Created role: " + roleName);
            } catch (Exception e) {
                logger.error("Error creating role " + roleName + ": " + e.getMessage(), e);
                throw new RuntimeException("Failed to create role: " + roleName, e);
            }
        } else {
            logger.info("Role already exists: " + roleName);
        }
        return role;
    }

    private void createUserIfNotFound(String password, String email, String firstName, String lastName, Collection<Role> roles) {
        User userOptional = userRepository.findByEmail(email);
        if (userOptional == null) {
            User user = new User();
            user.setPassword(passwordEncoder.encode(password));
            user.setEmail(email);
            user.setFirstname(firstName);
            user.setLastname(lastName);
            user.setRoles(new HashSet<>(roles));
            try {
                userService.save(user);
                logger.info("Created user: {}", email);
            } catch (Exception e) {
                logger.error("Error creating user {}: {}", email, e.getMessage());
                throw new RuntimeException("Failed to create user: " + email, e);
            }
        } else {
            logger.info("User  already exists: {}", email);
        }
    }
}
