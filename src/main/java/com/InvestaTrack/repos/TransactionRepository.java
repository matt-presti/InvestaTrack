package com.InvestaTrack.repos;

import com.InvestaTrack.models.Transaction;
import com.InvestaTrack.models.Transaction.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Find transactions by portfolio ID
    List<Transaction> findByPortfolioPortfolioID(Long portfolioId);

    // Find transactions by portfolio ID ordered by date
    List<Transaction> findByPortfolioPortfolioIDOrderByTransactionDateDesc(Long portfolioId);

    // Find transactions by portfolio ID and transaction type
    List<Transaction> findByPortfolioPortfolioIDAndTransactionType(Long portfolioId, TransactionType transactionType);

    // Find transactions by portfolio and stock
    List<Transaction> findByPortfolioPortfolioIDAndStockStockID(Long portfolioId, Long stockId);

    // Find transactions within date range
    @Query("SELECT t FROM Transaction t WHERE t.portfolio.portfolioID = :portfolioId " +
            "AND t.transactionDate BETWEEN :startDate AND :endDate " +
            "ORDER BY t.transactionDate DESC")
    List<Transaction> findByPortfolioAndDateRange(@Param("portfolioId") Long portfolioId,
                                                  @Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate);

    // Get recent transactions for a portfolio
    @Query("SELECT t FROM Transaction t JOIN FETCH t.stock WHERE t.portfolio.portfolioID = :portfolioId " +
            "ORDER BY t.transactionDate DESC")
    List<Transaction> findRecentTransactions(@Param("portfolioId") Long portfolioId);

    // Count transactions by type for a portfolio
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.portfolio.portfolioID = :portfolioId " +
            "AND t.transactionType = :type")
    Long countByPortfolioAndType(@Param("portfolioId") Long portfolioId, @Param("type") TransactionType type);
}