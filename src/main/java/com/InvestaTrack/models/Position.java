package com.InvestaTrack.models;

import com.fasterxml.jackson.annotation.*;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "positions")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "positionId")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Position implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "position_id")
    private Long positionId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @NotNull
    @Column(nullable = false)
    private Integer quantity = 0;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true, message = "Average cost must be non-negative")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal averageCost = BigDecimal.ZERO;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true, message = "Total cost must be non-negative")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalCost = BigDecimal.ZERO;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true, message = "Current value must be non-negative")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal currentValue = BigDecimal.ZERO;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public Position() {}

    public Position(Portfolio portfolio, Stock stock) {
        this.portfolio = portfolio;
        this.stock = stock;
    }

    // Getters and Setters
    public Long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getAverageCost() {
        return averageCost;
    }

    public void setAverageCost(BigDecimal averageCost) {
        this.averageCost = averageCost;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public BigDecimal getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(BigDecimal currentValue) {
        this.currentValue = currentValue;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Utility method to calculate gain/loss
    @JsonProperty("gainLoss")
    public BigDecimal getGainLoss() {
        if (currentValue == null || totalCost == null) {
            return BigDecimal.ZERO;
        }
        return currentValue.subtract(totalCost);
    }

    // Utility method to calculate gain/loss percentage
    @JsonProperty("gainLossPercentage")
    public BigDecimal getGainLossPercentage() {
        if (totalCost == null || totalCost.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal gainLoss = getGainLoss();
        return gainLoss.divide(totalCost, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100));
    }

    // Utility method to update current value based on stock price
    public void updateCurrentValue() {
        if (stock != null && stock.getCurrentPrice() != null && quantity != null) {
            this.currentValue = stock.getCurrentPrice().multiply(BigDecimal.valueOf(quantity));
        }
    }

    // Utility method for position summary
    @JsonProperty("summary")
    public String getSummary() {
        return quantity + " shares of " + (stock != null ? stock.getSymbol() : "Unknown") +
                " at avg cost $" + averageCost;
    }

    @Override
    public String toString() {
        return "Position{" +
                "positionId=" + positionId +
                ", portfolio=" + (portfolio != null ? portfolio.getPortfolioID() : "null") +
                ", stock=" + (stock != null ? stock.getSymbol() : "null") +
                ", quantity=" + quantity +
                ", averageCost=" + averageCost +
                ", totalCost=" + totalCost +
                ", currentValue=" + currentValue +
                ", updatedAt=" + updatedAt +
                '}';
    }
}