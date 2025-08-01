package com.InvestaTrack.repos;

import com.InvestaTrack.models.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {

    // Find positions by portfolio ID
    List<Position> findByPortfolioPortfolioID(Long portfolioId);

    // Find position by portfolio ID and stock ID
    Optional<Position> findByPortfolioPortfolioIDAndStockStockID(Long portfolioId, Long stockId);

    // Find active positions (quantity > 0) for a portfolio
    @Query("SELECT p FROM Position p WHERE p.portfolio.portfolioID = :portfolioId " +
            "AND p.quantity > 0 ORDER BY p.currentValue DESC")
    List<Position> findActivePositions(@Param("portfolioId") Long portfolioId);

    // Find positions with stock data loaded
    @Query("SELECT p FROM Position p JOIN FETCH p.stock WHERE p.portfolio.portfolioID = :portfolioId " +
            "ORDER BY p.currentValue DESC")
    List<Position> findByPortfolioWithStock(@Param("portfolioId") Long portfolioId);

    // Count active positions for a portfolio
    @Query("SELECT COUNT(p) FROM Position p WHERE p.portfolio.portfolioID = :portfolioId AND p.quantity > 0")
    Long countActivePositions(@Param("portfolioId") Long portfolioId);

    // Find top positions by value for a portfolio
    @Query("SELECT p FROM Position p JOIN FETCH p.stock WHERE p.portfolio.portfolioID = :portfolioId " +
            "AND p.quantity > 0 ORDER BY p.currentValue DESC")
    List<Position> findTopPositions(@Param("portfolioId") Long portfolioId);
}