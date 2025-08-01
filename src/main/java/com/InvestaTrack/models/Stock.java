package com.InvestaTrack.models;

import com.fasterxml.jackson.annotation.*;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stocks")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "stockID")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Stock implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id")
    private Long stockID;

    @NotNull
    @Size(min = 1, max = 10, message = "Stock symbol must be between 1 and 10 characters")
    @Column(unique = true, nullable = false, length = 10)
    private String symbol;

    @NotNull
    @Size(min = 1, max = 100, message = "Company name must be between 1 and 100 characters")
    @Column(nullable = false, length = 100)
    private String companyName;

    @DecimalMin(value = "0.0", inclusive = true, message = "Current price must be non-negative")
    @Column(precision = 10, scale = 2)
    private BigDecimal currentPrice = BigDecimal.ZERO;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime lastUpdated;

    @Size(max = 50, message = "Sector must be less than 50 characters")
    @Column(length = 50)
    private String sector;

    @DecimalMin(value = "0.0", inclusive = true, message = "Market cap must be non-negative")
    @Column(precision = 15, scale = 0)
    private BigDecimal marketCap = BigDecimal.ZERO;

    // Future relationships (commented out for now)
    // @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "stock")
    // @JsonIgnore
    // private Set<Transaction> transactions;

    // @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "stock")
    // @JsonIgnore
    // private Set<Position> positions;

    // Constructors
    public Stock() {}

    public Stock(String symbol, String companyName, BigDecimal currentPrice) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.currentPrice = currentPrice;
    }

    // Getters and Setters
    public Long getStockID() {
        return stockID;
    }

    public void setStockID(Long stockID) {
        this.stockID = stockID;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public BigDecimal getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(BigDecimal marketCap) {
        this.marketCap = marketCap;
    }

    // Utility method for display format (useful for API responses)
    @JsonProperty("displayName")
    public String getDisplayName() {
        return symbol + " - " + companyName;
    }

    @Override
    public String toString() {
        return "Stock{" +
                "stockID=" + stockID +
                ", symbol='" + symbol + '\'' +
                ", companyName='" + companyName + '\'' +
                ", currentPrice=" + currentPrice +
                ", sector='" + sector + '\'' +
                ", marketCap=" + marketCap +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}