package com.InvestaTrack.controllers;

import com.InvestaTrack.models.Stock;
import com.InvestaTrack.services.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Stock Management", description = "Operations for managing stock information and market data")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @Operation(
            summary = "Get all stocks",
            description = "Retrieve a complete list of all stocks in the system with current price information."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved all stocks",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Stock.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @GetMapping
    public ResponseEntity<List<Stock>> getAllStocks() {
        try {
            List<Stock> stocks = stockService.getAllStocks();
            return ResponseEntity.ok(stocks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
            summary = "Get stock by ID",
            description = "Retrieve detailed information for a specific stock using its unique identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Stock found and returned successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Stock.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Stock not found with the specified ID"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<Stock> getStockById(
            @Parameter(description = "Unique identifier of the stock", example = "1", required = true)
            @PathVariable Long id
    ) {
        try {
            Stock stock = stockService.getStockById(id);
            return ResponseEntity.ok(stock);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Get stock by symbol",
            description = "Retrieve stock information using the stock ticker symbol (e.g., AAPL, GOOGL)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Stock found by symbol",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Stock.class),
                            examples = @ExampleObject(value = "{\"stockID\": 1, \"symbol\": \"AAPL\", \"companyName\": \"Apple Inc.\", \"currentPrice\": 190.23}")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Stock not found with the specified symbol"
            )
    })
    @GetMapping("/symbol/{symbol}")
    public ResponseEntity<Stock> getStockBySymbol(
            @Parameter(description = "Stock ticker symbol", example = "AAPL", required = true)
            @PathVariable String symbol
    ) {
        try {
            Stock stock = stockService.getStockBySymbol(symbol);
            return ResponseEntity.ok(stock);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Search stocks",
            description = "Search for stocks by symbol or company name. Leave query empty to return all stocks."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Search completed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Stock.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Search failed due to server error"
            )
    })
    @GetMapping("/search")
    public ResponseEntity<List<Stock>> searchStocks(
            @Parameter(description = "Search term to match against stock symbol or company name", example = "Apple")
            @RequestParam(required = false) String query
    ) {
        try {
            List<Stock> stocks = stockService.searchStocks(query);
            return ResponseEntity.ok(stocks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
            summary = "Get stocks by sector",
            description = "Retrieve all stocks belonging to a specific market sector (e.g., Technology, Healthcare)."
    )
    @GetMapping("/sector/{sector}")
    public ResponseEntity<List<Stock>> getStocksBySector(
            @Parameter(description = "Market sector name", example = "Technology", required = true)
            @PathVariable String sector
    ) {
        try {
            List<Stock> stocks = stockService.getStocksBySector(sector);
            return ResponseEntity.ok(stocks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
            summary = "Get all sectors",
            description = "Retrieve a list of all available market sectors in the system."
    )
    @GetMapping("/sectors")
    public ResponseEntity<List<String>> getAllSectors() {
        try {
            List<String> sectors = stockService.getAllSectors();
            return ResponseEntity.ok(sectors);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
            summary = "Create new stock",
            description = "Add a new stock to the system with initial market information."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Stock created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Stock.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid stock data provided",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Stock symbol already exists\"}")
                    )
            )
    })
    @PostMapping
    public ResponseEntity<?> createStock(
            @Parameter(description = "Stock information including symbol, company name, and current price")
            @Valid @RequestBody Stock stock
    ) {
        try {
            Stock createdStock = stockService.createStock(stock);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdStock);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @Operation(
            summary = "Update stock information",
            description = "Update stock details such as company name, sector, or market cap. Price updates should use the specific price update endpoints."
    )
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStock(
            @Parameter(description = "Stock ID to update", example = "1", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated stock information")
            @Valid @RequestBody Stock stockDetails
    ) {
        try {
            Stock updatedStock = stockService.updateStock(id, stockDetails);
            return ResponseEntity.ok(updatedStock);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @Operation(
            summary = "Update stock price by ID",
            description = "Update the current market price for a specific stock using its ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Stock price updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Stock.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid price value",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Price must be greater than 0\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Stock not found"
            )
    })
    @PatchMapping("/{id}/price")
    public ResponseEntity<?> updateStockPrice(
            @Parameter(description = "Stock ID", example = "1", required = true)
            @PathVariable Long id,
            @Parameter(description = "New stock price", example = "195.50", required = true)
            @RequestParam BigDecimal price
    ) {
        try {
            Stock updatedStock = stockService.updateStockPrice(id, price);
            return ResponseEntity.ok(updatedStock);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @Operation(
            summary = "Update stock price by symbol",
            description = "Update the current market price for a specific stock using its ticker symbol."
    )
    @PatchMapping("/symbol/{symbol}/price")
    public ResponseEntity<?> updateStockPriceBySymbol(
            @Parameter(description = "Stock ticker symbol", example = "AAPL", required = true)
            @PathVariable String symbol,
            @Parameter(description = "New stock price", example = "195.50", required = true)
            @RequestParam BigDecimal price
    ) {
        try {
            Stock updatedStock = stockService.updateStockPriceBySymbol(symbol, price);
            return ResponseEntity.ok(updatedStock);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @Operation(
            summary = "Delete stock",
            description = "Remove a stock from the system. Note: This will also affect any associated transactions and positions."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStock(
            @Parameter(description = "Stock ID to delete", example = "1", required = true)
            @PathVariable Long id
    ) {
        try {
            stockService.deleteStock(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Stock with id " + id + " deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Get or create stock",
            description = "Retrieve an existing stock or create a new one if it doesn't exist. Useful for transaction processing."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Stock retrieved or created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Stock.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid stock data provided",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Invalid stock data: symbol is required\"}")
                    )
            )
    })
    @PostMapping("/get-or-create")
    public ResponseEntity<?> getOrCreateStock(
            @Parameter(description = "Stock data with symbol, companyName, and price fields")
            @RequestBody Map<String, Object> stockData
    ) {
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