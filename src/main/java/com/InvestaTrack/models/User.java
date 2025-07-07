package com.InvestaTrack.models;

import com.fasterxml.jackson.annotation.*;
//import jakarta.persistence.Entity;
//import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
//import jakarta.persistence.Id;

@Entity
@Table(name = "users")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    private String username;

    @JsonIgnore  // Hide email from JSON responses for privacy
    @NotNull
    @Column(unique = true)
    @Size(min = 6, max = 100)
    @Email(message = "Email should be valid")
    private String email;

    @JsonIgnore  // Never expose passwords in JSON responses
    @NotNull
    @Column(nullable = false)
    private String password;

    @NotNull
    @Size(min = 1, max = 50)
    private String firstName;

    @NotNull
    @Size(min = 1, max = 50)
    private String lastName;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @JsonIgnore  // Hide login timestamps from public API
    private LocalDateTime lastLoginAt;

    @NotNull
    @Column(nullable = false)
    private boolean isActive = true;

    @JsonIgnore  // Never expose transient password confirmation
    @Transient
    private String confirmPassword;

    @JsonIgnore  // Never expose reset tokens (security risk)
    private String resetPasswordToken;

    // Relationship to UserRoleJoin (for Spring Security)
    //@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST, mappedBy = "user")
    //@JsonIgnore  // Hide relationships from JSON when uncommented
    //private Set<UserRoleJoin> userRoles;

    // Relationship to Portfolios (one user can have many portfolios)
    //@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, mappedBy = "user")
    //@JsonIgnore  // Hide relationships from JSON when uncommented
    //private Set<Portfolio> portfolios;

    // Constructors
    public User() {}

    public User(String username, String email, String password, String firstName, String lastName) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Getters and Setters
    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getResetPasswordToken() {
        return resetPasswordToken;
    }

    public void setResetPasswordToken(String resetPasswordToken) {
        this.resetPasswordToken = resetPasswordToken;
    }

    public Long getId() {
        return id;
    }

    // Utility method for full name (useful for API responses)
    @JsonProperty("fullName")  // Include this computed property in JSON
    public String getFullName() {
        return firstName + " " + lastName;
    }
}