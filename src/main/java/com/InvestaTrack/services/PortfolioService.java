package com.InvestaTrack.services;

import com.InvestaTrack.models.Portfolio;
import com.InvestaTrack.models.Position;
import com.InvestaTrack.models.User;
import com.InvestaTrack.repos.PortfolioRepository;
import com.InvestaTrack.repos.PositionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
@Transactional
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final PositionRepository positionRepository;
    private final UserService userService;

    // Constructor injection
    public PortfolioService(PortfolioRepository portfolioRepository,
                            PositionRepository positionRepository,
                            UserService userService) {
        this.portfolioRepository = portfolioRepository;
        this.positionRepository = positionRepository;
        this.userService = userService;
    }

    // Get all portfolios with user data
    public List<Portfolio> getAllPortfolios() {
        return portfolioRepository.findAllWithUser();
    }

    // Get portfolio by ID with user data
    public Portfolio getPortfolioById(Long id) {
        return portfolioRepository.findByIdWithUser(id)
                .orElseThrow(() -> new RuntimeException("Portfolio not found with id: " + id));
    }

    // Get portfolios by user ID
    public List<Portfolio> getPortfoliosByUserId(Long userId) {
        return portfolioRepository.findByUserIdWithUser(userId);
    }

    // Create new portfolio
    public Portfolio createPortfolio(Portfolio portfolio) {
        // Validate user exists
        if (portfolio.getUser() == null || portfolio.getUser().getId() == null) {
            throw new RuntimeException("Portfolio must have a valid user");
        }

        // Ensure user exists
        User user = userService.getUserById(portfolio.getUser().getId());
        portfolio.setUser(user);

        // Save and return with user data
        Portfolio savedPortfolio = portfolioRepository.save(portfolio);
        return getPortfolioById(savedPortfolio.getPortfolioID());
    }

    // Update existing portfolio
    public Portfolio updatePortfolio(Long id, Portfolio portfolioDetails) {
        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Portfolio not found with id: " + id));

        // Update fields if provided
        if (portfolioDetails.getName() != null) {
            portfolio.setName(portfolioDetails.getName());
        }
        if (portfolioDetails.getDescription() != null) {
            portfolio.setDescription(portfolioDetails.getDescription());
        }
        if (portfolioDetails.getTotalValue() != null) {
            portfolio.setTotalValue(portfolioDetails.getTotalValue());
        }
        if (portfolioDetails.getTotalCost() != null) {
            portfolio.setTotalCost(portfolioDetails.getTotalCost());
        }

        Portfolio updatedPortfolio = portfolioRepository.save(portfolio);
        return getPortfolioById(updatedPortfolio.getPortfolioID());
    }

    // Delete portfolio
    public void deletePortfolio(Long id) {
        if (!portfolioRepository.existsById(id)) {
            throw new RuntimeException("Portfolio not found with id: " + id);
        }
        portfolioRepository.deleteById(id);
    }

    // Get portfolio count for user
    public Long getPortfolioCountByUserId(Long userId) {
        return portfolioRepository.countByUserId(userId);
    }

    // Update portfolio values based on positions
    public Portfolio updatePortfolioValues(Long portfolioId) {
        Portfolio portfolio = getPortfolioById(portfolioId);
        List<Position> positions = positionRepository.findActivePositions(portfolioId);

        BigDecimal totalValue = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;

        for (Position position : positions) {
            totalValue = totalValue.add(position.getCurrentValue());
            totalCost = totalCost.add(position.getTotalCost());
        }

        portfolio.setTotalValue(totalValue);
        portfolio.setTotalCost(totalCost);

        return portfolioRepository.save(portfolio);
    }

    // Get portfolio summary with statistics
    public Map<String, Object> getPortfolioSummary(Long portfolioId) {
        Portfolio portfolio = getPortfolioById(portfolioId);
        List<Position> positions = positionRepository.findActivePositions(portfolioId);

        Map<String, Object> summary = new HashMap<>();
        summary.put("portfolio", portfolio);
        summary.put("totalPositions", positions.size());
        summary.put("totalValue", portfolio.getTotalValue());
        summary.put("totalCost", portfolio.getTotalCost());
        summary.put("gainLoss", portfolio.getGainLoss());
        summary.put("gainLossPercentage", portfolio.getGainLossPercentage());
        summary.put("positions", positions);

        return summary;
    }

    // Get all portfolios for a user with summary
    public List<Map<String, Object>> getUserPortfoliosSummary(Long userId) {
        List<Portfolio> portfolios = getPortfoliosByUserId(userId);

        return portfolios.stream().map(portfolio -> {
            Map<String, Object> summary = new HashMap<>();
            summary.put("portfolio", portfolio);
            summary.put("positionCount", positionRepository.countActivePositions(portfolio.getPortfolioID()));
            summary.put("gainLoss", portfolio.getGainLoss());
            summary.put("gainLossPercentage", portfolio.getGainLossPercentage());
            return summary;
        }).toList();
    }

    public void deleteAllPortfolios() {
        portfolioRepository.deleteAll();
    }

}