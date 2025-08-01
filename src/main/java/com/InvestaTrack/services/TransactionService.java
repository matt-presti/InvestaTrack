package com.InvestaTrack.services;

import com.InvestaTrack.models.Transaction;
import com.InvestaTrack.models.Transaction.TransactionType;
import com.InvestaTrack.models.Portfolio;
import com.InvestaTrack.models.Stock;
import com.InvestaTrack.models.Position;
import com.InvestaTrack.repos.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final PortfolioService portfolioService;
    private final StockService stockService;
    private final PositionService positionService;

    // Constructor injection
    public TransactionService(TransactionRepository transactionRepository,
                              PortfolioService portfolioService,
                              StockService stockService,
                              PositionService positionService) {
        this.transactionRepository = transactionRepository;
        this.portfolioService = portfolioService;
        this.stockService = stockService;
        this.positionService = positionService;
    }

    // Get all transactions
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    // Get transaction by ID
    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
    }

    // Get transactions by portfolio ID
    public List<Transaction> getTransactionsByPortfolioId(Long portfolioId) {
        return transactionRepository.findByPortfolioPortfolioIDOrderByTransactionDateDesc(portfolioId);
    }

    // Create new transaction
    public Transaction createTransaction(Transaction transaction) {
        // Validate portfolio exists
        Portfolio portfolio = portfolioService.getPortfolioById(transaction.getPortfolio().getPortfolioID());
        transaction.setPortfolio(portfolio);

        // Validate stock exists
        Stock stock = stockService.getStockById(transaction.getStock().getStockID());
        transaction.setStock(stock);

        // Calculate total amount if not provided
        if (transaction.getTotalAmount() == null) {
            BigDecimal totalAmount = transaction.getPricePerShare()
                    .multiply(BigDecimal.valueOf(transaction.getQuantity()));
            transaction.setTotalAmount(totalAmount);
        }

        // Validate transaction based on type
        if (transaction.getTransactionType() == TransactionType.SELL) {
            validateSellTransaction(transaction);
        }

        // Ensure transactionDate is set
        if (transaction.getTransactionDate() == null) {
            transaction.setTransactionDate(LocalDateTime.now());
        }

        // Save transaction
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Update position
        positionService.updatePositionFromTransaction(savedTransaction);

        // Update portfolio values
        portfolioService.updatePortfolioValues(portfolio.getPortfolioID());

        return savedTransaction;
    }

    // Validate sell transaction
    private void validateSellTransaction(Transaction transaction) {
        Position position = positionService.getPositionByPortfolioAndStock(
                transaction.getPortfolio().getPortfolioID(),
                transaction.getStock().getStockID()
        );

        if (position == null || position.getQuantity() < transaction.getQuantity()) {
            throw new RuntimeException("Insufficient shares to sell. Available: " +
                    (position != null ? position.getQuantity() : 0));
        }
    }

    // Update transaction
    public Transaction updateTransaction(Long id, Transaction transactionDetails) {
        Transaction transaction = getTransactionById(id);

        // Only allow updating fees and notes (not core transaction details)
        if (transactionDetails.getFees() != null) {
            transaction.setFees(transactionDetails.getFees());
        }

        return transactionRepository.save(transaction);
    }

    // Delete transaction (with position recalculation)
    public void deleteTransaction(Long id) {
        Transaction transaction = getTransactionById(id);
        Long portfolioId = transaction.getPortfolio().getPortfolioID();
        Long stockId = transaction.getStock().getStockID();

        // Delete transaction
        transactionRepository.deleteById(id);

        // Recalculate position
        positionService.recalculatePosition(portfolioId, stockId);

        // Update portfolio values
        portfolioService.updatePortfolioValues(portfolioId);
    }

    // Get transactions by type
    public List<Transaction> getTransactionsByType(Long portfolioId, TransactionType type) {
        return transactionRepository.findByPortfolioPortfolioIDAndTransactionType(portfolioId, type);
    }

    // Get transactions within date range
    public List<Transaction> getTransactionsByDateRange(Long portfolioId,
                                                        LocalDateTime startDate,
                                                        LocalDateTime endDate) {
        return transactionRepository.findByPortfolioAndDateRange(portfolioId, startDate, endDate);
    }

    // Get recent transactions
    public List<Transaction> getRecentTransactions(Long portfolioId, int limit) {
        List<Transaction> allTransactions = transactionRepository.findRecentTransactions(portfolioId);
        return allTransactions.stream().limit(limit).toList();
    }

    // Get transaction summary for portfolio
    public Map<String, Object> getTransactionSummary(Long portfolioId) {
        List<Transaction> transactions = getTransactionsByPortfolioId(portfolioId);

        Long buyCount = transactionRepository.countByPortfolioAndType(portfolioId, TransactionType.BUY);
        Long sellCount = transactionRepository.countByPortfolioAndType(portfolioId, TransactionType.SELL);

        BigDecimal totalBuyAmount = BigDecimal.ZERO;
        BigDecimal totalSellAmount = BigDecimal.ZERO;
        BigDecimal totalFees = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {
            if (transaction.getTransactionType() == TransactionType.BUY) {
                totalBuyAmount = totalBuyAmount.add(transaction.getTotalAmount());
            } else {
                totalSellAmount = totalSellAmount.add(transaction.getTotalAmount());
            }
            totalFees = totalFees.add(transaction.getFees());
        }

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalTransactions", transactions.size());
        summary.put("buyTransactions", buyCount);
        summary.put("sellTransactions", sellCount);
        summary.put("totalBuyAmount", totalBuyAmount);
        summary.put("totalSellAmount", totalSellAmount);
        summary.put("totalFees", totalFees);
        summary.put("netInvested", totalBuyAmount.subtract(totalSellAmount));

        return summary;
    }

    // Create buy transaction helper
    public Transaction createBuyTransaction(Long portfolioId, Long stockId,
                                            Integer quantity, BigDecimal pricePerShare,
                                            BigDecimal fees) {
        Portfolio portfolio = portfolioService.getPortfolioById(portfolioId);
        Stock stock = stockService.getStockById(stockId);

        Transaction transaction = new Transaction(portfolio, stock, TransactionType.BUY,
                quantity, pricePerShare);
        if (fees != null) {
            transaction.setFees(fees);
        }

        return createTransaction(transaction);
    }

    // Create sell transaction helper
    public Transaction createSellTransaction(Long portfolioId, Long stockId,
                                             Integer quantity, BigDecimal pricePerShare,
                                             BigDecimal fees) {
        Portfolio portfolio = portfolioService.getPortfolioById(portfolioId);
        Stock stock = stockService.getStockById(stockId);

        Transaction transaction = new Transaction(portfolio, stock, TransactionType.SELL,
                quantity, pricePerShare);
        if (fees != null) {
            transaction.setFees(fees);
        }

        return createTransaction(transaction);
    }

    public void deleteAllTransactions() {
        transactionRepository.deleteAll();
    }

}