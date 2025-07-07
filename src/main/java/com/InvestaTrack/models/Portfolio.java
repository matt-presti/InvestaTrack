package com.InvestaTrack.models;

import com.fasterxml.jackson.annotation.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "portfolios")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "portfolioID")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})  // Add this line
public class Portfolio implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "portfolio_id")
    private Long portfolioID;

    @NotNull
    @Size(min = 1, max = 100, message = "Portfolio name must be between 1 and 100 characters")
    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @DecimalMin(value = "0.0", inclusive = true, message = "Total value must be non-negative")
    @Column(precision = 15, scale = 2)
    private BigDecimal totalValue = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", inclusive = true, message = "Total cost must be non-negative")
    @Column(precision = 15, scale = 2)
    private BigDecimal totalCost = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // JPA Relationship to User (Standard Spring Boot way)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Future relationships (commented out for now)
    // @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "portfolio")
    // private Set<Transaction> transactions;

    // @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "portfolio")
    // private Set<Position> positions;

    // Constructors
    public Portfolio() {}

    public Portfolio(User user, String name, String description) {
        this.user = user;
        this.name = name;
        this.description = description;
    }

    // Getters and Setters
    public Long getPortfolioID() {
        return portfolioID;
    }

    public void setPortfolioID(Long portfolioID) {
        this.portfolioID = portfolioID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // Utility method to calculate gain/loss
    public BigDecimal getGainLoss() {
        if (totalValue == null || totalCost == null) {
            return BigDecimal.ZERO;
        }
        return totalValue.subtract(totalCost);
    }

    // Utility method to calculate gain/loss percentage
    public BigDecimal getGainLossPercentage() {
        if (totalCost == null || totalCost.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal gainLoss = getGainLoss();
        return gainLoss.divide(totalCost, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100));
    }

    @Override
    public String toString() {
        return "Portfolio{" +
                "portfolioID=" + portfolioID +
                ", user=" + (user != null ? user.getUsername() : "null") +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", totalValue=" + totalValue +
                ", totalCost=" + totalCost +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}