package com.InvestaTrack.controllers;

import com.InvestaTrack.models.*;
import com.InvestaTrack.services.*;
import com.InvestaTrack.dto.TransactionDTO;
import com.InvestaTrack.dto.PositionDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @GetMapping("/load")
    @Transactional
    public String loadTestData() {
        // === USERS ===
        User alice = userService.createUser(new User("alice", "alice@test.com", "password123", "Alice", "Smith"));
        User bob = userService.createUser(new User("bob", "bob@test.com", "password123", "Bob", "Johnson"));

        // === STOCKS ===
        Stock aapl = stockService.getOrCreateStock("AAPL", "Apple Inc.", new BigDecimal("190.23"));
        Stock googl = stockService.getOrCreateStock("GOOGL", "Alphabet Inc.", new BigDecimal("125.45"));
        Stock msft = stockService.getOrCreateStock("MSFT", "Microsoft Corp.", new BigDecimal("310.00"));

        // === PORTFOLIOS ===
        Portfolio alicePortfolio = portfolioService.createPortfolio(new Portfolio(alice, "Growth Fund", "Alice's test portfolio"));
        Portfolio bobPortfolio = portfolioService.createPortfolio(new Portfolio(bob, "Conservative Fund", "Bob's test portfolio"));

        // === TRANSACTIONS ===
        transactionService.createBuyTransaction(alicePortfolio.getPortfolioID(), aapl.getStockID(), 10, aapl.getCurrentPrice(), new BigDecimal("4.95"));
        transactionService.createBuyTransaction(alicePortfolio.getPortfolioID(), msft.getStockID(), 5, msft.getCurrentPrice(), new BigDecimal("2.00"));
        transactionService.createSellTransaction(alicePortfolio.getPortfolioID(), msft.getStockID(), 2, msft.getCurrentPrice(), new BigDecimal("1.00"));

        transactionService.createBuyTransaction(bobPortfolio.getPortfolioID(), googl.getStockID(), 5, googl.getCurrentPrice(), new BigDecimal("3.95"));
        transactionService.createBuyTransaction(bobPortfolio.getPortfolioID(), aapl.getStockID(), 2, aapl.getCurrentPrice(), new BigDecimal("2.50"));

        // === FORCE POSITION AND PORTFOLIO UPDATES ===
        positionService.updatePortfolioPositionValues(alicePortfolio.getPortfolioID());
        positionService.updatePortfolioPositionValues(bobPortfolio.getPortfolioID());

        return "Test data loaded: 2 users, 2 portfolios, 3 stocks, 5 transactions (with auto-created positions)";
    }

    // === DATA RETRIEVAL ENDPOINTS ===

    @GetMapping("/summary")
    public List<Portfolio> getAllPortfolios() {
        return portfolioService.getAllPortfolios();
    }

    @GetMapping("/stocks")
    public List<Stock> getAllStocks() {
        return stockService.getAllStocks();
    }

    @GetMapping("/positions")
    @Transactional(readOnly = true)
    public List<PositionDTO> getAllPositions() {
        return positionService.getAllPositions()
                .stream()
                .map(PositionDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/transactions")
    @Transactional(readOnly = true)
    public List<TransactionDTO> getAllTransactions() {
        return transactionService.getAllTransactions()
                .stream()
                .map(TransactionDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/portfolio-summary/{portfolioId}")
    public Map<String, Object> getPortfolioSummary(@PathVariable Long portfolioId) {
        return transactionService.getTransactionSummary(portfolioId);
    }

    // === RESET FUNCTION ===

    @DeleteMapping("/clear")
    @Transactional
    public String clearAll() {
        // Clear in reverse dependency order
        positionService.deleteAllPositions();
        transactionService.deleteAllTransactions();
        portfolioService.deleteAllPortfolios();
        stockService.deleteAllStocks();
        userService.deleteAllUsers();

        // Reset primary key sequences
        entityManager.createNativeQuery("ALTER TABLE users ALTER COLUMN id RESTART WITH 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE portfolios ALTER COLUMN portfolio_id RESTART WITH 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE stocks ALTER COLUMN stock_id RESTART WITH 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE transactions ALTER COLUMN transaction_id RESTART WITH 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE positions ALTER COLUMN position_id RESTART WITH 1").executeUpdate();

        return "All test data cleared and sequences reset";
    }
}