package ru.kata.spring.boot_security.demo.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.kata.spring.boot_security.demo.entityes.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = {"roles"})
    @Query("SELECT u FROM User u")
    List<User> findAllWithRoles();

    @EntityGraph(attributePaths = {"roles"})
    User findByEmail(String email);
}