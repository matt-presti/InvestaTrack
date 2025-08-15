package com.InvestaTrack.controllers;

import com.InvestaTrack.models.Portfolio;
import com.InvestaTrack.dto.PortfolioDTO;
import com.InvestaTrack.services.PortfolioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Hidden;
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

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/portfolios")
@Tag(name = "Portfolio Management", description = "Operations for managing investment portfolios")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @Operation(
            summary = "Get portfolios by user ID",
            description = "Retrieve all portfolios belonging to a specific user as clean DTOs without exposing sensitive user information."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved user's portfolios",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PortfolioDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PortfolioDTO>> getPortfoliosByUserId(
            @Parameter(description = "User ID to retrieve portfolios for", example = "1", required = true)
            @PathVariable Long userId
    ) {
        try {
            List<PortfolioDTO> portfolios = portfolioService.getPortfoliosByUserId(userId)
                    .stream()
                    .map(PortfolioDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(portfolios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
            summary = "Get portfolio summary by ID",
            description = "Get comprehensive portfolio information including positions, current values, and performance calculations without exposing user details."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Portfolio summary retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"portfolioId\": 1, \"totalValue\": 15750.50, \"totalCost\": 13250.25, \"gainLoss\": 2500.25, \"positionCount\": 3}")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Portfolio not found"
            )
    })
    @GetMapping("/{id}/summary")
    public ResponseEntity<Map<String, Object>> getPortfolioSummary(
            @Parameter(description = "Portfolio ID for detailed summary", example = "1", required = true)
            @PathVariable Long id
    ) {
        try {
            Map<String, Object> summary = portfolioService.getPortfolioSummary(id);
            return ResponseEntity.ok(summary);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Update portfolio values",
            description = "Recalculate all position values in the portfolio using current market prices. Returns updated portfolio summary."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Portfolio values updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PortfolioDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid portfolio ID or update failed"
            )
    })
    @PostMapping("/{id}/update-values")
    public ResponseEntity<?> updatePortfolioValues(
            @Parameter(description = "Portfolio ID to update values for", example = "1", required = true)
            @PathVariable Long id
    ) {
        try {
            Portfolio updatedPortfolio = portfolioService.updatePortfolioValues(id);
            return ResponseEntity.ok(new PortfolioDTO(updatedPortfolio));
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // === HIDDEN ENDPOINTS - Full entity endpoints that expose user data ===

    @Hidden
    @GetMapping
    public ResponseEntity<List<PortfolioDTO>> getAllPortfolios() {
        try {
            List<PortfolioDTO> portfolios = portfolioService.getAllPortfolios()
                    .stream()
                    .map(PortfolioDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(portfolios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Hidden
    @GetMapping("/{id}")
    public ResponseEntity<PortfolioDTO> getPortfolioById(@PathVariable Long id) {
        try {
            Portfolio portfolio = portfolioService.getPortfolioById(id);
            return ResponseEntity.ok(new PortfolioDTO(portfolio));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Hidden
    @GetMapping("/user/{userId}/summary")
    public ResponseEntity<List<Map<String, Object>>> getUserPortfoliosSummary(@PathVariable Long userId) {
        try {
            List<Map<String, Object>> summary = portfolioService.getUserPortfoliosSummary(userId);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Hidden
    @PostMapping
    public ResponseEntity<?> createPortfolio(@Valid @RequestBody Portfolio portfolio) {
        try {
            Portfolio createdPortfolio = portfolioService.createPortfolio(portfolio);
            return ResponseEntity.status(HttpStatus.CREATED).body(new PortfolioDTO(createdPortfolio));
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @Hidden
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePortfolio(@PathVariable Long id, @Valid @RequestBody Portfolio portfolioDetails) {
        try {
            Portfolio updatedPortfolio = portfolioService.updatePortfolio(id, portfolioDetails);
            return ResponseEntity.ok(new PortfolioDTO(updatedPortfolio));
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @Hidden
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePortfolio(@PathVariable Long id) {
        try {
            portfolioService.deletePortfolio(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Portfolio with id " + id + " deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Hidden
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Map<String, Object>> getPortfolioCountByUserId(@PathVariable Long userId) {
        try {
            Long count = portfolioService.getPortfolioCountByUserId(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("portfolioCount", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}