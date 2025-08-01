package com.InvestaTrack.controllers;

import com.InvestaTrack.models.Transaction;
import com.InvestaTrack.models.Transaction.TransactionType;
import com.InvestaTrack.services.TransactionService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    // Constructor injection
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // Get all transactions
    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        try {
            List<Transaction> transactions = transactionService.getAllTransactions();
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get specific transaction by ID
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        try {
            Transaction transaction = transactionService.getTransactionById(id);
            return ResponseEntity.ok(transaction);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get transactions by portfolio ID
    @GetMapping("/portfolio/{portfolioId}")
    public ResponseEntity<List<Transaction>> getTransactionsByPortfolioId(@PathVariable Long portfolioId) {
        try {
            List<Transaction> transactions = transactionService.getTransactionsByPortfolioId(portfolioId);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get transactions by type
    @GetMapping("/portfolio/{portfolioId}/type/{type}")
    public ResponseEntity<List<Transaction>> getTransactionsByType(
            @PathVariable Long portfolioId,
            @PathVariable TransactionType type) {
        try {
            List<Transaction> transactions = transactionService.getTransactionsByType(portfolioId, type);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get transactions within date range
    @GetMapping("/portfolio/{portfolioId}/date-range")
    public ResponseEntity<List<Transaction>> getTransactionsByDateRange(
            @PathVariable Long portfolioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            List<Transaction> transactions = transactionService.getTransactionsByDateRange(portfolioId, startDate, endDate);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get recent transactions
    @GetMapping("/portfolio/{portfolioId}/recent")
    public ResponseEntity<List<Transaction>> getRecentTransactions(
            @PathVariable Long portfolioId,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<Transaction> transactions = transactionService.getRecentTransactions(portfolioId, limit);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get transaction summary for portfolio
    @GetMapping("/portfolio/{portfolioId}/summary")
    public ResponseEntity<Map<String, Object>> getTransactionSummary(@PathVariable Long portfolioId) {
        try {
            Map<String, Object> summary = transactionService.getTransactionSummary(portfolioId);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Create new transaction
    @PostMapping
    public ResponseEntity<?> createTransaction(@Valid @RequestBody Transaction transaction) {
        try {
            Transaction createdTransaction = transactionService.createTransaction(transaction);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTransaction);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Create buy transaction (simplified endpoint)
    @PostMapping("/buy")
    public ResponseEntity<?> createBuyTransaction(@RequestBody Map<String, Object> transactionData) {
        try {
            Long portfolioId = Long.parseLong(transactionData.get("portfolioId").toString());
            Long stockId = Long.parseLong(transactionData.get("stockId").toString());
            Integer quantity = Integer.parseInt(transactionData.get("quantity").toString());
            BigDecimal pricePerShare = new BigDecimal(transactionData.get("pricePerShare").toString());
            BigDecimal fees = transactionData.containsKey("fees") ?
                    new BigDecimal(transactionData.get("fees").toString()) : BigDecimal.ZERO;

            Transaction transaction = transactionService.createBuyTransaction(
                    portfolioId, stockId, quantity, pricePerShare, fees
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid transaction data: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Create sell transaction (simplified endpoint)
    @PostMapping("/sell")
    public ResponseEntity<?> createSellTransaction(@RequestBody Map<String, Object> transactionData) {
        try {
            Long portfolioId = Long.parseLong(transactionData.get("portfolioId").toString());
            Long stockId = Long.parseLong(transactionData.get("stockId").toString());
            Integer quantity = Integer.parseInt(transactionData.get("quantity").toString());
            BigDecimal pricePerShare = new BigDecimal(transactionData.get("pricePerShare").toString());
            BigDecimal fees = transactionData.containsKey("fees") ?
                    new BigDecimal(transactionData.get("fees").toString()) : BigDecimal.ZERO;

            Transaction transaction = transactionService.createSellTransaction(
                    portfolioId, stockId, quantity, pricePerShare, fees
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid transaction data: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Update transaction (limited to fees)
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateTransaction(@PathVariable Long id, @RequestBody Transaction transactionDetails) {
        try {
            Transaction updatedTransaction = transactionService.updateTransaction(id, transactionDetails);
            return ResponseEntity.ok(updatedTransaction);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Delete transaction
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable Long id) {
        try {
            transactionService.deleteTransaction(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Transaction with id " + id + " deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}