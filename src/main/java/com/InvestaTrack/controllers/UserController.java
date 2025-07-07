package com.InvestaTrack.controllers;

import com.InvestaTrack.models.User;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping
    public List<User> getAllUsers() {
        return entityManager.createQuery("SELECT u FROM User u ORDER BY u.id", User.class).getResultList();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return entityManager.find(User.class, id);
    }

    @PostMapping
    @Transactional
    public User createUser(@RequestBody User user) {
        entityManager.persist(user);
        entityManager.flush();
        return user;
    }

    @DeleteMapping("/{id}")
    @Transactional
    public String deleteUser(@PathVariable Long id) {
        User user = entityManager.find(User.class, id);
        if (user != null) {
            entityManager.remove(user);
            return "User deleted";
        }
        return "User not found";
    }
}