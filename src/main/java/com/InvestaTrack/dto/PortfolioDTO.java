package com.InvestaTrack.dto;

import com.InvestaTrack.models.Portfolio;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PortfolioDTO {
    private Long portfolioId;
    private String name;
    private String description;
    private BigDecimal totalValue;
    private BigDecimal totalCost;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String userName;  // Just the name, not full user object
    private BigDecimal gainLoss;
    private BigDecimal gainLossPercentage;

    // Constructor that converts from Portfolio entity
    public PortfolioDTO(Portfolio portfolio) {
        this.portfolioId = portfolio.getPortfolioID();
        this.name = portfolio.getName();
        this.description = portfolio.getDescription();
        this.totalValue = portfolio.getTotalValue();
        this.totalCost = portfolio.getTotalCost();
        this.createdAt = portfolio.getCreatedAt();
        this.updatedAt = portfolio.getUpdatedAt();
        // Only include user's display name, not full user object
        this.userName = portfolio.getUser() != null ? portfolio.getUser().getFullName() : null;
        this.gainLoss = portfolio.getGainLoss();
        this.gainLossPercentage = portfolio.getGainLossPercentage();
    }

    // Getters and Setters
    public Long getPortfolioId() { return portfolioId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getTotalValue() { return totalValue; }
    public BigDecimal getTotalCost() { return totalCost; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public String getUserName() { return userName; }
    public BigDecimal getGainLoss() { return gainLoss; }
    public BigDecimal getGainLossPercentage() { return gainLossPercentage; }

    public void setPortfolioId(Long portfolioId) { this.portfolioId = portfolioId; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setGainLoss(BigDecimal gainLoss) { this.gainLoss = gainLoss; }
    public void setGainLossPercentage(BigDecimal gainLossPercentage) { this.gainLossPercentage = gainLossPercentage; }
}