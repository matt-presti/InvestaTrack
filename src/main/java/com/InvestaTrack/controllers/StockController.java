package com.InvestaTrack.controllers;

import com.InvestaTrack.models.Stock;
import com.InvestaTrack.services.StockService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/stocks")
public class StockController {

    private final StockService stockService;

    // Constructor injection
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    // Get all stocks
    @GetMapping
    public ResponseEntity<List<Stock>> getAllStocks() {
        try {
            List<Stock> stocks = stockService.getAllStocks();
            return ResponseEntity.ok(stocks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get specific stock by ID
    @GetMapping("/{id}")
    public ResponseEntity<Stock> getStockById(@PathVariable Long id) {
        try {
            Stock stock = stockService.getStockById(id);
            return ResponseEntity.ok(stock);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get stock by symbol
    @GetMapping("/symbol/{symbol}")
    public ResponseEntity<Stock> getStockBySymbol(@PathVariable String symbol) {
        try {
            Stock stock = stockService.getStockBySymbol(symbol);
            return ResponseEntity.ok(stock);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Search stocks
    @GetMapping("/search")
    public ResponseEntity<List<Stock>> searchStocks(@RequestParam(required = false) String query) {
        try {
            List<Stock> stocks = stockService.searchStocks(query);
            return ResponseEntity.ok(stocks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get stocks by sector
    @GetMapping("/sector/{sector}")
    public ResponseEntity<List<Stock>> getStocksBySector(@PathVariable String sector) {
        try {
            List<Stock> stocks = stockService.getStocksBySector(sector);
            return ResponseEntity.ok(stocks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get all sectors
    @GetMapping("/sectors")
    public ResponseEntity<List<String>> getAllSectors() {
        try {
            List<String> sectors = stockService.getAllSectors();
            return ResponseEntity.ok(sectors);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Create new stock
    @PostMapping
    public ResponseEntity<?> createStock(@Valid @RequestBody Stock stock) {
        try {
            Stock createdStock = stockService.createStock(stock);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdStock);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Update existing stock
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStock(@PathVariable Long id, @Valid @RequestBody Stock stockDetails) {
        try {
            Stock updatedStock = stockService.updateStock(id, stockDetails);
            return ResponseEntity.ok(updatedStock);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Update stock price
    @PatchMapping("/{id}/price")
    public ResponseEntity<?> updateStockPrice(@PathVariable Long id, @RequestParam BigDecimal price) {
        try {
            Stock updatedStock = stockService.updateStockPrice(id, price);
            return ResponseEntity.ok(updatedStock);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Update stock price by symbol
    @PatchMapping("/symbol/{symbol}/price")
    public ResponseEntity<?> updateStockPriceBySymbol(@PathVariable String symbol, @RequestParam BigDecimal price) {
        try {
            Stock updatedStock = stockService.updateStockPriceBySymbol(symbol, price);
            return ResponseEntity.ok(updatedStock);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Delete stock
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStock(@PathVariable Long id) {
        try {
            stockService.deleteStock(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Stock with id " + id + " deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get or create stock (useful for transaction creation)
    @PostMapping("/get-or-create")
    public ResponseEntity<?> getOrCreateStock(@RequestBody Map<String, Object> stockData) {
        try {
            String symbol = (String) stockData.get("symbol");
            String companyName = (String) stockData.get("companyName");
            BigDecimal price = new BigDecimal(stockData.get("price").toString());

            Stock stock = stockService.getOrCreateStock(symbol, companyName, price);
            return ResponseEntity.ok(stock);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid stock data: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}