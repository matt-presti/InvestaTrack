package com.InvestaTrack.controllers;

import com.InvestaTrack.models.*;
import com.InvestaTrack.services.*;
import com.InvestaTrack.dto.TransactionDTO;
import com.InvestaTrack.dto.PositionDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/test")
public class TestDataController {

    @Autowired private UserService userService;
    @Autowired private PortfolioService portfolioService;
    @Autowired private StockService stockService;
    @Autowired private TransactionService transactionService;
    @Autowired private PositionService positionService;

    @PersistenceContext
    private EntityManager entityManager;

    private static final Logger logger = LoggerFactory.getLogger(TestDataController.class);

    // === BASE ENDPOINT ===
    @GetMapping("/")
    public ResponseEntity<String> testEndpointInfo() {
        return ResponseEntity.ok("Test endpoints available: /load, /clear, /summary, /stocks, /transactions, /positions");
    }

    // === MAIN DATA LOADING ===
    @GetMapping("/load")
    @Transactional
    public ResponseEntity<String> loadTestData() {
        try {
            logger.info("Starting to load test data...");

            // Clear existing data first to avoid conflicts
            clearAllData();

            // === USERS ===
            logger.info("Creating users...");
            User alice = userService.createUser(new User("alice", "alice@test.com", "password123", "Alice", "Smith"));
            User bob = userService.createUser(new User("bob", "bob@test.com", "password123", "Bob", "Johnson"));

            // === STOCKS ===
            logger.info("Creating stocks...");
            Stock aapl = stockService.getOrCreateStock("AAPL", "Apple Inc.", new BigDecimal("190.23"));
            Stock googl = stockService.getOrCreateStock("GOOGL", "Alphabet Inc.", new BigDecimal("125.45"));
            Stock msft = stockService.getOrCreateStock("MSFT", "Microsoft Corp.", new BigDecimal("310.00"));

            // === PORTFOLIOS ===
            logger.info("Creating portfolios...");
            Portfolio alicePortfolio = portfolioService.createPortfolio(new Portfolio(alice, "Growth Fund", "Alice's test portfolio"));
            Portfolio bobPortfolio = portfolioService.createPortfolio(new Portfolio(bob, "Conservative Fund", "Bob's test portfolio"));

            // === TRANSACTIONS ===
            logger.info("Creating transactions...");
            transactionService.createBuyTransaction(alicePortfolio.getPortfolioID(), aapl.getStockID(), 10, aapl.getCurrentPrice(), new BigDecimal("4.95"));
            transactionService.createBuyTransaction(alicePortfolio.getPortfolioID(), msft.getStockID(), 5, msft.getCurrentPrice(), new BigDecimal("2.00"));
            transactionService.createSellTransaction(alicePortfolio.getPortfolioID(), msft.getStockID(), 2, msft.getCurrentPrice(), new BigDecimal("1.00"));

            transactionService.createBuyTransaction(bobPortfolio.getPortfolioID(), googl.getStockID(), 5, googl.getCurrentPrice(), new BigDecimal("3.95"));
            transactionService.createBuyTransaction(bobPortfolio.getPortfolioID(), aapl.getStockID(), 2, aapl.getCurrentPrice(), new BigDecimal("2.50"));

            // === FORCE POSITION AND PORTFOLIO UPDATES ===
            logger.info("Updating portfolio positions...");
            positionService.updatePortfolioPositionValues(alicePortfolio.getPortfolioID());
            positionService.updatePortfolioPositionValues(bobPortfolio.getPortfolioID());

            String message = "Test data loaded successfully: 2 users, 2 portfolios, 3 stocks, 5 transactions (with auto-created positions)";
            logger.info(message);
            return ResponseEntity.ok(message);

        } catch (Exception e) {
            logger.error("Error loading test data: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error loading test data: " + e.getMessage());
        }
    }

    // === ALTERNATIVE SAFE LOAD (checks for existing data) ===
    @GetMapping("/load-safe")
    @Transactional
    public ResponseEntity<String> loadTestDataSafe() {
        try {
            logger.info("Checking for existing test data...");

            // Check if data already exists
            if (!userService.getAllUsers().isEmpty()) {
                return ResponseEntity.ok("Test data already exists. Use /clear first or /load to force reload.");
            }

            // If no data exists, load it
            return loadTestData();

        } catch (Exception e) {
            logger.error("Error in safe load: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error loading test data: " + e.getMessage());
        }
    }

    // === DATA RETRIEVAL ENDPOINTS ===

    @GetMapping("/summary")
    public ResponseEntity<List<Portfolio>> getAllPortfolios() {
        try {
            List<Portfolio> portfolios = portfolioService.getAllPortfolios();
            return ResponseEntity.ok(portfolios);
        } catch (Exception e) {
            logger.error("Error getting portfolios: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/stocks")
    public ResponseEntity<List<Stock>> getAllStocks() {
        try {
            List<Stock> stocks = stockService.getAllStocks();
            return ResponseEntity.ok(stocks);
        } catch (Exception e) {
            logger.error("Error getting stocks: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/positions")
    @Transactional(readOnly = true)
    public ResponseEntity<List<PositionDTO>> getAllPositions() {
        try {
            List<PositionDTO> positions = positionService.getAllPositions()
                    .stream()
                    .map(PositionDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(positions);
        } catch (Exception e) {
            logger.error("Error getting positions: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/transactions")
    @Transactional(readOnly = true)
    public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
        try {
            List<TransactionDTO> transactions = transactionService.getAllTransactions()
                    .stream()
                    .map(TransactionDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            logger.error("Error getting transactions: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/portfolio-summary/{portfolioId}")
    public ResponseEntity<Map<String, Object>> getPortfolioSummary(@PathVariable Long portfolioId) {
        try {
            Map<String, Object> summary = transactionService.getTransactionSummary(portfolioId);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            logger.error("Error getting portfolio summary: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // === DATA CLEARING ===

    @DeleteMapping("/clear")
    @Transactional
    public ResponseEntity<String> clearAll() {
        try {
            logger.info("Starting to clear all test data...");
            clearAllData();
            String message = "All test data cleared and sequences reset";
            logger.info(message);
            return ResponseEntity.ok(message);

        } catch (Exception e) {
            logger.error("Error clearing data: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error clearing data: " + e.getMessage());
        }
    }

    // === HELPER METHOD FOR CLEARING DATA ===
    private void clearAllData() {
        logger.info("Clearing all data in dependency order...");

        // Clear in reverse dependency order
        positionService.deleteAllPositions();
        transactionService.deleteAllTransactions();
        portfolioService.deleteAllPortfolios();
        stockService.deleteAllStocks();
        userService.deleteAllUsers();

        // Reset primary key sequences
        try {
            entityManager.createNativeQuery("ALTER TABLE users ALTER COLUMN id RESTART WITH 1").executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE portfolios ALTER COLUMN portfolio_id RESTART WITH 1").executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE stocks ALTER COLUMN stock_id RESTART WITH 1").executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE transactions ALTER COLUMN transaction_id RESTART WITH 1").executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE positions ALTER COLUMN position_id RESTART WITH 1").executeUpdate();
            logger.info("Database sequences reset successfully");
        } catch (Exception e) {
            logger.warn("Could not reset sequences (might not be H2/PostgreSQL): " + e.getMessage());
        }

        entityManager.flush();
        entityManager.clear();
    }

    // === STATUS ENDPOINT ===
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getDataStatus() {
        try {
            Map<String, Object> status = Map.of(
                    "users", userService.getAllUsers().size(),
                    "portfolios", portfolioService.getAllPortfolios().size(),
                    "stocks", stockService.getAllStocks().size(),
                    "transactions", transactionService.getAllTransactions().size(),
                    "positions", positionService.getAllPositions().size()
            );
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            logger.error("Error getting status: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // === DEBUG ENDPOINTS (can be removed in production) ===

    @GetMapping("/load-debug")
    @Transactional
    public ResponseEntity<String> loadTestDataDebug() {
        try {
            User alice = userService.createUser(new User("alice-debug", "alice-debug@test.com", "password123", "Alice", "Smith"));
            return ResponseEntity.ok("Debug Step 1 passed: User created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Debug Step 1 failed - User creation error: " + e.getMessage());
        }
    }

    @GetMapping("/load-debug2")
    @Transactional
    public ResponseEntity<String> loadTestDataDebug2() {
        try {
            User alice = userService.createUser(new User("alice-debug2", "alice-debug2@test.com", "password123", "Alice", "Smith"));
            Stock aapl = stockService.getOrCreateStock("AAPL-DEBUG", "Apple Inc. Debug", new BigDecimal("190.23"));
            return ResponseEntity.ok("Debug Step 2 passed: User and Stock created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Debug Step 2 failed: " + e.getMessage());
        }
    }

    @GetMapping("/load-debug3")
    @Transactional
    public ResponseEntity<String> loadTestDataDebug3() {
        try {
            User alice = userService.createUser(new User("alice-debug3", "alice-debug3@test.com", "password123", "Alice", "Smith"));
            Stock aapl = stockService.getOrCreateStock("AAPL-DEBUG3", "Apple Inc. Debug3", new BigDecimal("190.23"));
            Portfolio alicePortfolio = portfolioService.createPortfolio(new Portfolio(alice, "Debug Growth Fund", "Alice's debug portfolio"));
            return ResponseEntity.ok("Debug Step 3 passed: User, Stock, and Portfolio created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Debug Step 3 failed: " + e.getMessage());
        }
    }

    @GetMapping("/load-debug4")
    @Transactional
    public ResponseEntity<String> loadTestDataDebug4() {
        try {
            User alice = userService.createUser(new User("alice-debug4", "alice-debug4@test.com", "password123", "Alice", "Smith"));
            Stock aapl = stockService.getOrCreateStock("AAPL-DEBUG4", "Apple Inc. Debug4", new BigDecimal("190.23"));
            Portfolio alicePortfolio = portfolioService.createPortfolio(new Portfolio(alice, "Debug Growth Fund", "Alice's debug portfolio"));
            transactionService.createBuyTransaction(alicePortfolio.getPortfolioID(), aapl.getStockID(), 10, aapl.getCurrentPrice(), new BigDecimal("4.95"));
            return ResponseEntity.ok("Debug Step 4 passed: Full flow with transaction created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Debug Step 4 failed - Transaction creation error: " + e.getMessage());
        }
    }
}