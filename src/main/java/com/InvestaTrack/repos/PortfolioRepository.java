package com.InvestaTrack.repos;

import com.InvestaTrack.models.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    // Find portfolios by user ID
    List<Portfolio> findByUserId(Long userId);

    // Find portfolios by user ID ordered by creation date
    List<Portfolio> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Count portfolios for a user
    Long countByUserId(Long userId);

    // Find portfolio by ID with user data loaded
    @Query("SELECT p FROM Portfolio p JOIN FETCH p.user WHERE p.portfolioID = :id")
    Optional<Portfolio> findByIdWithUser(@Param("id") Long id);

    // Find all portfolios with user data loaded
    @Query("SELECT p FROM Portfolio p JOIN FETCH p.user ORDER BY p.portfolioID")
    List<Portfolio> findAllWithUser();

    // Find portfolios by user ID with user data loaded
    @Query("SELECT p FROM Portfolio p JOIN FETCH p.user WHERE p.user.id = :userId ORDER BY p.portfolioID")
    List<Portfolio> findByUserIdWithUser(@Param("userId") Long userId);
}