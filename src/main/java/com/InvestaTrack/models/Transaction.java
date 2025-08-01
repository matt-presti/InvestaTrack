package com.InvestaTrack.models;

import com.fasterxml.jackson.annotation.*;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.DecimalMin;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "transactionId")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Transaction implements Serializable {

    public enum TransactionType {
        BUY, SELL
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TransactionType transactionType;

    @NotNull
    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(nullable = false)
    private Integer quantity;

    @NotNull
    @DecimalMin(value = "0.01", message = "Price per share must be greater than 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerShare;

    @NotNull
    @DecimalMin(value = "0.01", message = "Total amount must be greater than 0")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @NotNull
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime transactionDate;

    @DecimalMin(value = "0.0", inclusive = true, message = "Fees must be non-negative")
    @Column(precision = 8, scale = 2)
    private BigDecimal fees = BigDecimal.ZERO;

    // Constructors
    public Transaction() {}

    public Transaction(Portfolio portfolio, Stock stock, TransactionType transactionType,
                       Integer quantity, BigDecimal pricePerShare) {
        this.portfolio = portfolio;
        this.stock = stock;
        this.transactionType = transactionType;
        this.quantity = quantity;
        this.pricePerShare = pricePerShare;
        this.totalAmount = pricePerShare.multiply(BigDecimal.valueOf(quantity));
    }

    // Getters and Setters
    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPricePerShare() {
        return pricePerShare;
    }

    public void setPricePerShare(BigDecimal pricePerShare) {
        this.pricePerShare = pricePerShare;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public BigDecimal getFees() {
        return fees;
    }

    public void setFees(BigDecimal fees) {
        this.fees = fees;
    }

    // Utility method to calculate net amount (useful for portfolio calculations)
    @JsonProperty("netAmount")
    public BigDecimal getNetAmount() {
        if (transactionType == TransactionType.BUY) {
            return totalAmount.add(fees);
        } else {
            return totalAmount.subtract(fees);
        }
    }

    // Utility method for transaction description
    @JsonProperty("description")
    public String getDescription() {
        return transactionType + " " + quantity + " shares of " +
                (stock != null ? stock.getSymbol() : "Unknown") +
                " @ $" + pricePerShare;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", portfolio=" + (portfolio != null ? portfolio.getPortfolioID() : "null") +
                ", stock=" + (stock != null ? stock.getSymbol() : "null") +
                ", transactionType=" + transactionType +
                ", quantity=" + quantity +
                ", pricePerShare=" + pricePerShare +
                ", totalAmount=" + totalAmount +
                ", fees=" + fees +
                ", transactionDate=" + transactionDate +
                '}';
    }
}