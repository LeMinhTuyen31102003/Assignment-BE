package com.example.quizz.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI (Swagger) Configuration
 * Configures API documentation with JWT authentication support
 */
@Configuration
public class OpenApiConfig {
    private static final String BEARER_AUTH = "bearerAuth";
    @Bean
    public OpenAPI quizOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8080");
        localServer.setDescription("Development Server");

        Contact contact = new Contact();
        contact.setName("Quiz Application Team");
        contact.setEmail("support@quizapp.com");

        License license = new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");

        Info info = new Info()
                .title("Quiz Application REST API")
                .version("1.0.0")
                .contact(contact)
                .description("This API provides endpoints for managing quizzes, questions, and answers. " +
                        "Built with Spring Boot 4.x and Java 21. " +
                        "Authentication: JWT Bearer Token. " +
                        "Use /api/v1/auth/register or /api/v1/auth/login to get your token.")
                .license(license);

        // JWT Security Scheme
        SecurityScheme securityScheme = new SecurityScheme()
                .name(BEARER_AUTH)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Enter JWT token obtained from /api/v1/auth/login");

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList(BEARER_AUTH);

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer))
                .components(new Components().addSecuritySchemes(BEARER_AUTH, securityScheme))
                .addSecurityItem(securityRequirement);
    }
}
