package com.InvestaTrack.dto;

import com.InvestaTrack.models.Position;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PositionDTO {
    private Long positionId;
    private Long portfolioId;
    private String portfolioName;
    private Long stockId;
    private String stockSymbol;
    private String stockName;
    private Integer quantity;
    private BigDecimal averageCost;
    private BigDecimal totalCost;
    private BigDecimal currentValue;
    private LocalDateTime updatedAt;
    private String summary;
    private BigDecimal gainLoss;
    private BigDecimal gainLossPercentage;

    // Constructor that converts from Position entity
    public PositionDTO(Position position) {
        this.positionId = position.getPositionId();
        this.portfolioId = position.getPortfolio().getPortfolioID();
        this.portfolioName = position.getPortfolio().getName();
        this.stockId = position.getStock().getStockID();
        this.stockSymbol = position.getStock().getSymbol();
        this.stockName = position.getStock().getCompanyName();
        this.quantity = position.getQuantity();
        this.averageCost = position.getAverageCost();
        this.totalCost = position.getTotalCost();
        this.currentValue = position.getCurrentValue();
        this.updatedAt = position.getUpdatedAt();
        this.summary = position.getSummary();
        this.gainLoss = position.getGainLoss();
        this.gainLossPercentage = position.getGainLossPercentage();
    }

    // Getters
    public Long getPositionId() { return positionId; }
    public Long getPortfolioId() { return portfolioId; }
    public String getPortfolioName() { return portfolioName; }
    public Long getStockId() { return stockId; }
    public String getStockSymbol() { return stockSymbol; }
    public String getStockName() { return stockName; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getAverageCost() { return averageCost; }
    public BigDecimal getTotalCost() { return totalCost; }
    public BigDecimal getCurrentValue() { return currentValue; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public String getSummary() { return summary; }
    public BigDecimal getGainLoss() { return gainLoss; }
    public BigDecimal getGainLossPercentage() { return gainLossPercentage; }

    // Setters (if needed for flexibility)
    public void setPositionId(Long positionId) { this.positionId = positionId; }
    public void setPortfolioId(Long portfolioId) { this.portfolioId = portfolioId; }
    public void setPortfolioName(String portfolioName) { this.portfolioName = portfolioName; }
    public void setStockId(Long stockId) { this.stockId = stockId; }
    public void setStockSymbol(String stockSymbol) { this.stockSymbol = stockSymbol; }
    public void setStockName(String stockName) { this.stockName = stockName; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setAverageCost(BigDecimal averageCost) { this.averageCost = averageCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
    public void setCurrentValue(BigDecimal currentValue) { this.currentValue = currentValue; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setSummary(String summary) { this.summary = summary; }
    public void setGainLoss(BigDecimal gainLoss) { this.gainLoss = gainLoss; }
    public void setGainLossPercentage(BigDecimal gainLossPercentage) { this.gainLossPercentage = gainLossPercentage; }
}