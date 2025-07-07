package com.InvestaTrack.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Configure authorization (similar to your friend's antMatchers)
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/h2-console/**").permitAll()  // Allow H2 console
                        .requestMatchers("/api/**").authenticated()     // Require auth for API
                        .requestMatchers("/test/**").authenticated()    // Require auth for test endpoints
                        .anyRequest().authenticated()
                )
                // Enable HTTP Basic authentication
                .httpBasic(httpBasic -> {})
                // Enable logout (like your friend's config)
                .logout(logout -> logout.permitAll())
                // Enable CORS (like your friend's config)
                .cors(cors -> {})
                // Disable CSRF (like your friend's config)
                .csrf(csrf -> csrf.disable());

        // Allow H2 console to work in frames
        http.headers(headers -> headers.frameOptions().sameOrigin());

        return http.build();
    }
}