package com.InvestaTrack.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI investaTrackOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("InvestaTrack API")
                        .description("Investment Portfolio Tracker REST API built with Spring Boot. " +
                                "This API allows users to manage investment portfolios, track transactions, " +
                                "and monitor stock performance.")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Matthew Presti")
                                .email("mapresti3@gmail.com")
                                .url("https://matt-presti.github.io/"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development Server")
                ));
    }
}