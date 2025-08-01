package com.InvestaTrack.repos;

import com.InvestaTrack.models.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    // Find stock by symbol
    Optional<Stock> findBySymbol(String symbol);

    // Find stocks by sector ordered by company name
    List<Stock> findBySectorOrderByCompanyName(String sector);

    // Search stocks by symbol or company name (case-insensitive)
    @Query("SELECT s FROM Stock s WHERE LOWER(s.symbol) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(s.companyName) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "ORDER BY s.symbol")
    List<Stock> searchStocks(@Param("search") String search);

    // Find all distinct sectors
    @Query("SELECT DISTINCT s.sector FROM Stock s WHERE s.sector IS NOT NULL ORDER BY s.sector")
    List<String> findAllSectors();

    // Check if symbol exists
    boolean existsBySymbol(String symbol);

    // Find stocks by multiple symbols
    List<Stock> findBySymbolIn(List<String> symbols);
}