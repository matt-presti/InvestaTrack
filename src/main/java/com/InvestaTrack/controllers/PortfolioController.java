package com.InvestaTrack.controllers;

import com.InvestaTrack.models.Portfolio;
import com.InvestaTrack.services.PortfolioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/portfolios")
public class PortfolioController {

    private final PortfolioService portfolioService;

    // Constructor injection
    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    // Get all portfolios with user data
    @GetMapping
    public ResponseEntity<List<Portfolio>> getAllPortfolios() {
        try {
            List<Portfolio> portfolios = portfolioService.getAllPortfolios();
            return ResponseEntity.ok(portfolios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get specific portfolio by ID with user data
    @GetMapping("/{id}")
    public ResponseEntity<Portfolio> getPortfolioById(@PathVariable Long id) {
        try {
            Portfolio portfolio = portfolioService.getPortfolioById(id);
            return ResponseEntity.ok(portfolio);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get all portfolios for a specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Portfolio>> getPortfoliosByUserId(@PathVariable Long userId) {
        try {
            List<Portfolio> portfolios = portfolioService.getPortfoliosByUserId(userId);
            return ResponseEntity.ok(portfolios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get portfolios summary for a user
    @GetMapping("/user/{userId}/summary")
    public ResponseEntity<List<Map<String, Object>>> getUserPortfoliosSummary(@PathVariable Long userId) {
        try {
            List<Map<String, Object>> summary = portfolioService.getUserPortfoliosSummary(userId);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Create new portfolio
    @PostMapping
    public ResponseEntity<?> createPortfolio(@Valid @RequestBody Portfolio portfolio) {
        try {
            Portfolio createdPortfolio = portfolioService.createPortfolio(portfolio);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPortfolio);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Update existing portfolio
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePortfolio(@PathVariable Long id, @Valid @RequestBody Portfolio portfolioDetails) {
        try {
            Portfolio updatedPortfolio = portfolioService.updatePortfolio(id, portfolioDetails);
            return ResponseEntity.ok(updatedPortfolio);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Delete portfolio
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePortfolio(@PathVariable Long id) {
        try {
            portfolioService.deletePortfolio(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Portfolio with id " + id + " deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get portfolio count for a user
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Map<String, Object>> getPortfolioCountByUserId(@PathVariable Long userId) {
        try {
            Long count = portfolioService.getPortfolioCountByUserId(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("portfolioCount", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get portfolio summary with positions
    @GetMapping("/{id}/summary")
    public ResponseEntity<Map<String, Object>> getPortfolioSummary(@PathVariable Long id) {
        try {
            Map<String, Object> summary = portfolioService.getPortfolioSummary(id);
            return ResponseEntity.ok(summary);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Update portfolio values
    @PostMapping("/{id}/update-values")
    public ResponseEntity<?> updatePortfolioValues(@PathVariable Long id) {
        try {
            Portfolio updatedPortfolio = portfolioService.updatePortfolioValues(id);
            return ResponseEntity.ok(updatedPortfolio);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}