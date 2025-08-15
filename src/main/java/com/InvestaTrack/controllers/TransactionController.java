package com.InvestaTrack.controllers;

import com.InvestaTrack.models.Transaction;
import com.InvestaTrack.models.Transaction.TransactionType;
import com.InvestaTrack.dto.TransactionDTO;
import com.InvestaTrack.services.TransactionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transaction Management", description = "Operations for managing buy/sell transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // Get all transactions as DTOs
    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
        try {
            List<TransactionDTO> transactions = transactionService.getAllTransactions()
                    .stream()
                    .map(TransactionDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get transaction by ID as DTO
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable Long id) {
        try {
            Transaction transaction = transactionService.getTransactionById(id);
            return ResponseEntity.ok(new TransactionDTO(transaction));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get transactions by portfolio as DTOs
    @GetMapping("/portfolio/{portfolioId}")
    @Transactional(readOnly = true)
    public ResponseEntity<List<TransactionDTO>> getTransactionsByPortfolioId(@PathVariable Long portfolioId) {
        try {
            List<TransactionDTO> transactions = transactionService.getTransactionsByPortfolioId(portfolioId)
                    .stream()
                    .map(TransactionDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get transactions by type as DTOs
    @GetMapping("/portfolio/{portfolioId}/type/{type}")
    @Transactional(readOnly = true)
    public ResponseEntity<List<TransactionDTO>> getTransactionsByType(
            @PathVariable Long portfolioId,
            @PathVariable TransactionType type) {
        try {
            List<TransactionDTO> transactions = transactionService.getTransactionsByType(portfolioId, type)
                    .stream()
                    .map(TransactionDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get recent transactions as DTOs
    @GetMapping("/portfolio/{portfolioId}/recent")
    @Transactional(readOnly = true)
    public ResponseEntity<List<TransactionDTO>> getRecentTransactions(
            @PathVariable Long portfolioId,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<TransactionDTO> transactions = transactionService.getRecentTransactions(portfolioId, limit)
                    .stream()
                    .map(TransactionDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get transaction summary
    @GetMapping("/portfolio/{portfolioId}/summary")
    public ResponseEntity<Map<String, Object>> getTransactionSummary(@PathVariable Long portfolioId) {
        try {
            Map<String, Object> summary = transactionService.getTransactionSummary(portfolioId);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Create buy transaction
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
            return ResponseEntity.status(HttpStatus.CREATED).body(new TransactionDTO(transaction));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid transaction data: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Create sell transaction
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
            return ResponseEntity.status(HttpStatus.CREATED).body(new TransactionDTO(transaction));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid transaction data: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Date range transactions
    @GetMapping("/portfolio/{portfolioId}/date-range")
    @Transactional(readOnly = true)
    public ResponseEntity<List<TransactionDTO>> getTransactionsByDateRange(
            @PathVariable Long portfolioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            List<TransactionDTO> transactions = transactionService.getTransactionsByDateRange(portfolioId, startDate, endDate)
                    .stream()
                    .map(TransactionDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Create general transaction
    @PostMapping
    public ResponseEntity<?> createTransaction(@Valid @RequestBody Transaction transaction) {
        try {
            Transaction createdTransaction = transactionService.createTransaction(transaction);
            return ResponseEntity.status(HttpStatus.CREATED).body(new TransactionDTO(createdTransaction));
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Update transaction
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateTransaction(@PathVariable Long id, @RequestBody Transaction transactionDetails) {
        try {
            Transaction updatedTransaction = transactionService.updateTransaction(id, transactionDetails);
            return ResponseEntity.ok(new TransactionDTO(updatedTransaction));
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