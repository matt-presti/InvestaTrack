package com.InvestaTrack.controllers;

import com.InvestaTrack.models.Portfolio;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;

@RestController
@RequestMapping("/api/portfolios")
public class PortfolioController {

    @PersistenceContext
    private EntityManager entityManager;

    // Get all portfolios with user data
    @GetMapping
    public List<Portfolio> getAllPortfolios() {
        return entityManager.createQuery(
                "SELECT p FROM Portfolio p JOIN FETCH p.user ORDER BY p.portfolioID",
                Portfolio.class
        ).getResultList();
    }

    // Get specific portfolio by ID with user data
    @GetMapping("/{id}")
    public Portfolio getPortfolioById(@PathVariable Long id) {
        List<Portfolio> results = entityManager.createQuery(
                "SELECT p FROM Portfolio p JOIN FETCH p.user WHERE p.portfolioID = :id",
                Portfolio.class
        ).setParameter("id", id).getResultList();

        if (results.isEmpty()) {
            throw new RuntimeException("Portfolio not found with id: " + id);
        }

        return results.get(0);
    }

    // Get all portfolios for a specific user
    @GetMapping("/user/{userId}")
    public List<Portfolio> getPortfoliosByUserId(@PathVariable Long userId) {
        return entityManager.createQuery(
                "SELECT p FROM Portfolio p JOIN FETCH p.user WHERE p.user.id = :userId ORDER BY p.portfolioID",
                Portfolio.class
        ).setParameter("userId", userId).getResultList();
    }

    // Create new portfolio
    @PostMapping
    @Transactional
    public Portfolio createPortfolio(@RequestBody Portfolio portfolio) {
        // Validate that user exists
        if (portfolio.getUser() == null || portfolio.getUser().getId() == null) {
            throw new RuntimeException("Portfolio must have a valid user");
        }

        entityManager.persist(portfolio);
        entityManager.flush();

        portfolio.getUser().getUsername(); // This triggers the lazy loading


        // Return the portfolio with user data
        return getPortfolioById(portfolio.getPortfolioID());
    }

    // Update existing portfolio
    @PutMapping("/{id}")
    @Transactional
    public Portfolio updatePortfolio(@PathVariable Long id, @RequestBody Portfolio portfolioDetails) {
        Portfolio portfolio = entityManager.find(Portfolio.class, id);

        if (portfolio == null) {
            throw new RuntimeException("Portfolio not found with id: " + id);
        }

        // Update fields
        if (portfolioDetails.getName() != null) {
            portfolio.setName(portfolioDetails.getName());
        }
        if (portfolioDetails.getDescription() != null) {
            portfolio.setDescription(portfolioDetails.getDescription());
        }
        if (portfolioDetails.getTotalValue() != null) {
            portfolio.setTotalValue(portfolioDetails.getTotalValue());
        }
        if (portfolioDetails.getTotalCost() != null) {
            portfolio.setTotalCost(portfolioDetails.getTotalCost());
        }

        Portfolio updatedPortfolio = entityManager.merge(portfolio);
        entityManager.flush();

        // Return updated portfolio with user data
        return getPortfolioById(updatedPortfolio.getPortfolioID());
    }

    // Delete portfolio
    @DeleteMapping("/{id}")
    @Transactional
    public String deletePortfolio(@PathVariable Long id) {
        Portfolio portfolio = entityManager.find(Portfolio.class, id);

        if (portfolio == null) {
            return "Portfolio not found with id: " + id;
        }

        entityManager.remove(portfolio);
        return "Portfolio with id " + id + " deleted successfully";
    }

    // Get portfolio count for a user
    @GetMapping("/user/{userId}/count")
    public String getPortfolioCountByUserId(@PathVariable Long userId) {
        Long count = entityManager.createQuery(
                "SELECT COUNT(p) FROM Portfolio p WHERE p.user.id = :userId",
                Long.class
        ).setParameter("userId", userId).getSingleResult();

        return "User " + userId + " has " + count + " portfolio(s)";
    }
}