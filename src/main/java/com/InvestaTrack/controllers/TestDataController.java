package com.InvestaTrack.controllers;

import com.InvestaTrack.models.User;
import com.InvestaTrack.models.Portfolio;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestDataController {

    @PersistenceContext
    private EntityManager entityManager;

    // Add test users
    @GetMapping("/add-users")
    @Transactional
    public String addTestUsers() {
        try {
            String[][] testUsers = {
                    {"alice", "Alice", "Smith", "alice@test.com"},
                    {"bob", "Bob", "Johnson", "bob@test.com"},
                    {"charlie", "Charlie", "Williams", "charlie@test.com"}
            };

            for (String[] userData : testUsers) {
                User user = new User();
                user.setUsername(userData[0]);
                user.setFirstName(userData[1]);
                user.setLastName(userData[2]);
                user.setEmail(userData[3]);
                user.setPassword("password123");

                entityManager.persist(user);
            }

            entityManager.flush();

            // Add test portfolios
            List<User> users = entityManager.createQuery("SELECT u FROM User u", User.class).getResultList();
            String[] portfolioNames = {"Growth Portfolio", "Conservative Portfolio", "Tech Portfolio"};

            for (int i = 0; i < users.size(); i++) {
                Portfolio portfolio = new Portfolio();
                portfolio.setUser(users.get(i));
                portfolio.setName(portfolioNames[i]);
                portfolio.setDescription("Test portfolio for " + users.get(i).getFirstName());
                portfolio.setTotalValue(new BigDecimal("10000.00"));
                portfolio.setTotalCost(new BigDecimal("9500.00"));
                entityManager.persist(portfolio);
            }

            return "SUCCESS! Added 3 users and 3 portfolios";
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    // Clear specific data
    @DeleteMapping("/clear/users")
    @Transactional
    public String clearUsers() {
        int deleted = entityManager.createQuery("DELETE FROM User u").executeUpdate();
        return "Cleared " + deleted + " users";
    }

    @DeleteMapping("/clear/portfolios")
    @Transactional
    public String clearPortfolios() {
        int deleted = entityManager.createQuery("DELETE FROM Portfolio p").executeUpdate();
        return "Cleared " + deleted + " portfolios";
    }

    @DeleteMapping("/clear/all")
    @Transactional
    public String clearAll() {
        int portfolios = entityManager.createQuery("DELETE FROM Portfolio p").executeUpdate();
        int users = entityManager.createQuery("DELETE FROM User u").executeUpdate();
        return "Cleared " + users + " users and " + portfolios + " portfolios";
    }
}