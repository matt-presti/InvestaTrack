package com.InvestaTrack.dto;

import com.InvestaTrack.models.Transaction;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionDTO {
    private Long transactionId;
    private Long portfolioId;
    private String portfolioName;
    private Long stockId;
    private String stockSymbol;
    private String stockName;
    private String transactionType;
    private Integer quantity;
    private BigDecimal pricePerShare;
    private BigDecimal totalAmount;
    private LocalDateTime transactionDate;
    private BigDecimal fees;
    private String description;
    private BigDecimal netAmount;

    // Constructor that converts from Transaction entity
    public TransactionDTO(Transaction transaction) {
        this.transactionId = transaction.getTransactionId();
        this.portfolioId = transaction.getPortfolio().getPortfolioID();
        this.portfolioName = transaction.getPortfolio().getName();
        this.stockId = transaction.getStock().getStockID();
        this.stockSymbol = transaction.getStock().getSymbol();
        this.stockName = transaction.getStock().getCompanyName();
        this.transactionType = transaction.getTransactionType().toString();
        this.quantity = transaction.getQuantity();
        this.pricePerShare = transaction.getPricePerShare();
        this.totalAmount = transaction.getTotalAmount();
        this.transactionDate = transaction.getTransactionDate();
        this.fees = transaction.getFees();
        this.description = transaction.getDescription();
        this.netAmount = transaction.getNetAmount();
    }

    // Getters
    public Long getTransactionId() { return transactionId; }
    public Long getPortfolioId() { return portfolioId; }
    public String getPortfolioName() { return portfolioName; }
    public Long getStockId() { return stockId; }
    public String getStockSymbol() { return stockSymbol; }
    public String getStockName() { return stockName; }
    public String getTransactionType() { return transactionType; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getPricePerShare() { return pricePerShare; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public LocalDateTime getTransactionDate() { return transactionDate; }
    public BigDecimal getFees() { return fees; }
    public String getDescription() { return description; }
    public BigDecimal getNetAmount() { return netAmount; }

    // Setters
    public void setTransactionId(Long transactionId) { this.transactionId = transactionId; }
    public void setPortfolioId(Long portfolioId) { this.portfolioId = portfolioId; }
    public void setPortfolioName(String portfolioName) { this.portfolioName = portfolioName; }
    public void setStockId(Long stockId) { this.stockId = stockId; }
    public void setStockSymbol(String stockSymbol) { this.stockSymbol = stockSymbol; }
    public void setStockName(String stockName) { this.stockName = stockName; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setPricePerShare(BigDecimal pricePerShare) { this.pricePerShare = pricePerShare; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }
    public void setFees(BigDecimal fees) { this.fees = fees; }
    public void setDescription(String description) { this.description = description; }
    public void setNetAmount(BigDecimal netAmount) { this.netAmount = netAmount; }
}