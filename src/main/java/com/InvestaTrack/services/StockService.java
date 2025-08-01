package com.InvestaTrack.services;

import com.InvestaTrack.models.Stock;
import com.InvestaTrack.repos.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StockService {

    private final StockRepository stockRepository;

    // Constructor injection
    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    // Get all stocks
    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    // Get stock by ID
    public Stock getStockById(Long id) {
        return stockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock not found with id: " + id));
    }

    // Get stock by symbol
    public Stock getStockBySymbol(String symbol) {
        return stockRepository.findBySymbol(symbol.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Stock not found with symbol: " + symbol));
    }

    // Create new stock
    public Stock createStock(Stock stock) {
        // Ensure symbol is uppercase
        stock.setSymbol(stock.getSymbol().toUpperCase());

        // Check if symbol already exists
        if (stockRepository.existsBySymbol(stock.getSymbol())) {
            throw new RuntimeException("Stock already exists with symbol: " + stock.getSymbol());
        }

        return stockRepository.save(stock);
    }

    // Update existing stock
    public Stock updateStock(Long id, Stock stockDetails) {
        Stock stock = getStockById(id);

        // Update fields if provided
        if (stockDetails.getCompanyName() != null) {
            stock.setCompanyName(stockDetails.getCompanyName());
        }
        if (stockDetails.getCurrentPrice() != null) {
            stock.setCurrentPrice(stockDetails.getCurrentPrice());
        }
        if (stockDetails.getSector() != null) {
            stock.setSector(stockDetails.getSector());
        }
        if (stockDetails.getMarketCap() != null) {
            stock.setMarketCap(stockDetails.getMarketCap());
        }

        return stockRepository.save(stock);
    }

    // Delete stock
    public void deleteStock(Long id) {
        if (!stockRepository.existsById(id)) {
            throw new RuntimeException("Stock not found with id: " + id);
        }
        stockRepository.deleteById(id);
    }

    // Search stocks by symbol or company name
    public List<Stock> searchStocks(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllStocks();
        }
        return stockRepository.searchStocks(searchTerm.trim());
    }

    // Get stocks by sector
    public List<Stock> getStocksBySector(String sector) {
        return stockRepository.findBySectorOrderByCompanyName(sector);
    }

    // Get all sectors
    public List<String> getAllSectors() {
        return stockRepository.findAllSectors();
    }

    // Update stock price
    public Stock updateStockPrice(Long id, BigDecimal newPrice) {
        Stock stock = getStockById(id);
        stock.setCurrentPrice(newPrice);
        stock.setLastUpdated(LocalDateTime.now());
        return stockRepository.save(stock);
    }

    // Update stock price by symbol
    public Stock updateStockPriceBySymbol(String symbol, BigDecimal newPrice) {
        Stock stock = getStockBySymbol(symbol);
        stock.setCurrentPrice(newPrice);
        stock.setLastUpdated(LocalDateTime.now());
        return stockRepository.save(stock);
    }

    // Get or create stock
    public Stock getOrCreateStock(String symbol, String companyName, BigDecimal price) {
        Optional<Stock> existingStock = stockRepository.findBySymbol(symbol.toUpperCase());

        if (existingStock.isPresent()) {
            // Update price if stock exists
            Stock stock = existingStock.get();
            if (price != null) {
                stock.setCurrentPrice(price);
                stock.setLastUpdated(LocalDateTime.now());
                return stockRepository.save(stock);
            }
            return stock;
        } else {
            // Create new stock
            Stock newStock = new Stock(symbol.toUpperCase(), companyName, price);
            return stockRepository.save(newStock);
        }
    }

    // Get multiple stocks by symbols
    public List<Stock> getStocksBySymbols(List<String> symbols) {
        List<String> upperSymbols = symbols.stream()
                .map(String::toUpperCase)
                .toList();
        return stockRepository.findBySymbolIn(upperSymbols);
    }

    public void deleteAllStocks() {
        stockRepository.deleteAll();
    }

}