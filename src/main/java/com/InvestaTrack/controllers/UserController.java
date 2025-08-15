package com.InvestaTrack.controllers;

import com.InvestaTrack.models.User;
import com.InvestaTrack.services.UserService;
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

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "Operations for managing user accounts and authentication")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Get all users",
            description = "Retrieve a list of all registered users in the system. Sensitive information like passwords and emails are hidden."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved all users",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = User.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
            summary = "Get user by ID",
            description = "Retrieve detailed information for a specific user using their unique identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User found and returned successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = User.class),
                            examples = @ExampleObject(value = "{\"id\": 1, \"username\": \"alice\", \"firstName\": \"Alice\", \"lastName\": \"Smith\", \"fullName\": \"Alice Smith\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found with the specified ID"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(
            @Parameter(description = "Unique identifier of the user", example = "1", required = true)
            @PathVariable Long id
    ) {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Get user by username",
            description = "Retrieve user information using their unique username."
    )
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(
            @Parameter(description = "Unique username", example = "alice", required = true)
            @PathVariable String username
    ) {
        try {
            User user = userService.getUserByUsername(username);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Create new user",
            description = "Register a new user account in the system. Username and email must be unique."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User account created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = User.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid user data or username/email already exists",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Username already exists\"}")
                    )
            )
    })
    @PostMapping
    public ResponseEntity<?> createUser(
            @Parameter(description = "User registration information including username, email, password, and personal details")
            @Valid @RequestBody User user
    ) {
        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @Operation(
            summary = "Update user information",
            description = "Update user profile information such as name and email. Username cannot be changed after registration."
    )
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @Parameter(description = "User ID to update", example = "1", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated user information")
            @Valid @RequestBody User userDetails
    ) {
        try {
            User updatedUser = userService.updateUser(id, userDetails);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @Operation(
            summary = "Delete user account",
            description = "Permanently delete a user account and all associated portfolios and transactions. This action cannot be undone."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User account deleted successfully",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"User with id 1 deleted successfully\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(
            @Parameter(description = "User ID to delete", example = "1", required = true)
            @PathVariable Long id
    ) {
        try {
            userService.deleteUser(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "User with id " + id + " deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Check username availability",
            description = "Verify if a username is already taken in the system. Useful for registration forms."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Username availability check completed",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"exists\": true}")
                    )
            )
    })
    @GetMapping("/check-username/{username}")
    public ResponseEntity<Map<String, Boolean>> checkUsername(
            @Parameter(description = "Username to check for availability", example = "alice", required = true)
            @PathVariable String username
    ) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", userService.usernameExists(username));
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Check email availability",
            description = "Verify if an email address is already registered in the system."
    )
    @GetMapping("/check-email/{email}")
    public ResponseEntity<Map<String, Boolean>> checkEmail(
            @Parameter(description = "Email address to check", example = "alice@example.com", required = true)
            @PathVariable String email
    ) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", userService.emailExists(email));
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Activate or deactivate user",
            description = "Enable or disable a user account. Inactive users cannot log in or perform operations."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User status updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = User.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    @PatchMapping("/{id}/active")
    public ResponseEntity<?> setUserActive(
            @Parameter(description = "User ID to update", example = "1", required = true)
            @PathVariable Long id,
            @Parameter(description = "Active status to set", example = "true", required = true)
            @RequestParam boolean active
    ) {
        try {
            User user = userService.setUserActive(id, active);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}