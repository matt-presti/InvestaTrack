package com.InvestaTrack.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/h2-console/**").permitAll()  // H2 database console
                        .requestMatchers("/test/**").permitAll()        // Test endpoints
                        .requestMatchers("/api/**").permitAll()         // API endpoints
                        .requestMatchers("/").permitAll()               // Root path
                        .requestMatchers("/static/**").permitAll()      // Static resources
                        .requestMatchers("/favicon.ico").permitAll()    // Favicon
                        .requestMatchers("/manifest.json").permitAll()  // React manifest
                        // Swagger/OpenAPI endpoints
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-resources/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()
                        .anyRequest().permitAll()                       // Allow all for development
                )
                .httpBasic(httpBasic -> httpBasic.disable())    // Disable basic auth for cleaner development
                .formLogin(form -> form.disable())              // Disable form login
                .logout(logout -> logout.permitAll())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))  // Proper CORS config
                .csrf(csrf -> csrf.disable());                  // Disable CSRF for API development

        // Allow H2 console to be embedded in frames (updated for newer Spring Security)
        http.headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin())
        );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow requests from your React development server
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",    // React dev server default
                "http://localhost:3001",    // Alternative React port
                "http://127.0.0.1:3000",
                "http://127.0.0.1:3001"
        ));

        // Allow all HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // Allow all headers
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // Allow credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);

        // Cache preflight requests for 1 hour
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}