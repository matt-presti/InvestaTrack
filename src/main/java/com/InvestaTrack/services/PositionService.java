package com.InvestaTrack.services;

import com.InvestaTrack.models.Position;
import com.InvestaTrack.models.Transaction;
import com.InvestaTrack.models.Transaction.TransactionType;
import com.InvestaTrack.models.Portfolio;
import com.InvestaTrack.models.Stock;
import com.InvestaTrack.repos.PositionRepository;
import com.InvestaTrack.repos.TransactionRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PositionService {

    private final PositionRepository positionRepository;
    private final TransactionRepository transactionRepository;
    private final PortfolioService portfolioService;
    private final StockService stockService;

    // Constructor injection with @Lazy to avoid circular dependency
    public PositionService(PositionRepository positionRepository,
                           TransactionRepository transactionRepository,
                           @Lazy PortfolioService portfolioService,
                           StockService stockService) {
        this.positionRepository = positionRepository;
        this.transactionRepository = transactionRepository;
        this.portfolioService = portfolioService;
        this.stockService = stockService;
    }

    // Get all positions
    public List<Position> getAllPositions() {
        return positionRepository.findAll();
    }

    // Get position by ID
    public Position getPositionById(Long id) {
        return positionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Position not found with id: " + id));
    }

    // Get positions by portfolio ID
    public List<Position> getPositionsByPortfolioId(Long portfolioId) {
        return positionRepository.findByPortfolioWithStock(portfolioId);
    }

    // Get active positions by portfolio ID
    public List<Position> getActivePositions(Long portfolioId) {
        return positionRepository.findActivePositions(portfolioId);
    }

    // Get position by portfolio and stock
    public Position getPositionByPortfolioAndStock(Long portfolioId, Long stockId) {
        Optional<Position> position = positionRepository.findByPortfolioPortfolioIDAndStockStockID(portfolioId, stockId);
        return position.orElse(null);
    }

    // Update position from transaction
    public Position updatePositionFromTransaction(Transaction transaction) {
        Position position = getOrCreatePosition(
                transaction.getPortfolio().getPortfolioID(),
                transaction.getStock().getStockID()
        );

        if (transaction.getTransactionType() == TransactionType.BUY) {
            // Calculate new total cost
            BigDecimal newTotalCost = position.getTotalCost()
                    .add(transaction.getTotalAmount())
                    .add(transaction.getFees());

            // Calculate new quantity
            int newQuantity = position.getQuantity() + transaction.getQuantity();

            // Calculate new average cost
            BigDecimal newAverageCost = newTotalCost.divide(
                    BigDecimal.valueOf(newQuantity), 2, RoundingMode.HALF_UP
            );

            position.setQuantity(newQuantity);
            position.setTotalCost(newTotalCost);
            position.setAverageCost(newAverageCost);

        } else { // SELL
            // Calculate new quantity
            int newQuantity = position.getQuantity() - transaction.getQuantity();

            if (newQuantity < 0) {
                throw new RuntimeException("Cannot sell more shares than owned");
            }

            // Calculate proportional cost reduction
            if (newQuantity == 0) {
                position.setQuantity(0);
                position.setTotalCost(BigDecimal.ZERO);
                position.setAverageCost(BigDecimal.ZERO);
            } else {
                BigDecimal costReduction = position.getAverageCost()
                        .multiply(BigDecimal.valueOf(transaction.getQuantity()));
                BigDecimal newTotalCost = position.getTotalCost().subtract(costReduction);

                position.setQuantity(newQuantity);
                position.setTotalCost(newTotalCost);
                // Average cost remains the same for sells
            }
        }

        // Update current value
        position.updateCurrentValue();

        return positionRepository.save(position);
    }

    // Get or create position
    private Position getOrCreatePosition(Long portfolioId, Long stockId) {
        Optional<Position> existingPosition = positionRepository
                .findByPortfolioPortfolioIDAndStockStockID(portfolioId, stockId);

        if (existingPosition.isPresent()) {
            return existingPosition.get();
        }

        // Create new position
        Portfolio portfolio = portfolioService.getPortfolioById(portfolioId);
        Stock stock = stockService.getStockById(stockId);

        Position newPosition = new Position(portfolio, stock);
        return positionRepository.save(newPosition);
    }

    // Recalculate position from all transactions
    public Position recalculatePosition(Long portfolioId, Long stockId) {
        Position position = getOrCreatePosition(portfolioId, stockId);

        // Get all transactions for this portfolio and stock
        List<Transaction> transactions = transactionRepository
                .findByPortfolioPortfolioIDAndStockStockID(portfolioId, stockId);

        // Reset position
        position.setQuantity(0);
        position.setTotalCost(BigDecimal.ZERO);
        position.setAverageCost(BigDecimal.ZERO);

        BigDecimal totalCost = BigDecimal.ZERO;
        int totalQuantity = 0;

        // Recalculate from all transactions
        for (Transaction transaction : transactions) {
            if (transaction.getTransactionType() == TransactionType.BUY) {
                totalQuantity += transaction.getQuantity();
                totalCost = totalCost.add(transaction.getTotalAmount()).add(transaction.getFees());
            } else { // SELL
                totalQuantity -= transaction.getQuantity();
                if (totalQuantity > 0) {
                    // Proportionally reduce cost
                    BigDecimal costReduction = position.getAverageCost()
                            .multiply(BigDecimal.valueOf(transaction.getQuantity()));
                    totalCost = totalCost.subtract(costReduction);
                } else if (totalQuantity == 0) {
                    totalCost = BigDecimal.ZERO;
                }
            }
        }

        position.setQuantity(totalQuantity);
        position.setTotalCost(totalCost);

        if (totalQuantity > 0) {
            position.setAverageCost(totalCost.divide(
                    BigDecimal.valueOf(totalQuantity), 2, RoundingMode.HALF_UP
            ));
        } else {
            position.setAverageCost(BigDecimal.ZERO);
        }

        // Update current value
        position.updateCurrentValue();

        return positionRepository.save(position);
    }

    // Update all position values for a portfolio
    public void updatePortfolioPositionValues(Long portfolioId) {
        List<Position> positions = positionRepository.findByPortfolioPortfolioID(portfolioId);

        for (Position position : positions) {
            position.updateCurrentValue();
            positionRepository.save(position);
        }
    }

    // Get portfolio positions summary
    public List<Position> getTopPositions(Long portfolioId, int limit) {
        List<Position> positions = positionRepository.findTopPositions(portfolioId);
        return positions.stream().limit(limit).toList();
    }

    // Count active positions
    public Long countActivePositions(Long portfolioId) {
        return positionRepository.countActivePositions(portfolioId);
    }

    public void deleteAllPositions() {
        positionRepository.deleteAll();
    }

}