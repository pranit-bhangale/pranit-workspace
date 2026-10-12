package com.pranit.workspace.shared.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Global OpenAPI / Swagger configuration for Pranit Portfolio Application.
 * Configures API metadata, zero-trust authentication schemes (HttpOnly Cookie & Bearer JWT),
 * server environments, and module tagging for all 14 controller endpoints.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Pranit Portfolio Application API",
                version = "1.0.0",
                summary = "Production-grade RESTful API powering Pranit Bhangale's engineering portfolio and developer showcase platform.",
                description = """
                        ### Overview
                        REST API for the **Pranit Portfolio Application**, engineered with **Spring Boot 4** and **Java 25**.
                        Provides high-throughput, low-latency endpoints for managing professional showcase projects, technical competencies,
                        work experience, academic credentials, competitive programming achievements, technical blog publications,
                        and visitor communication channels.
                        
                        ### Key Architectural Capabilities
                        - **Zero-Trust Security**: Asymmetric RSA-2048 signed JWTs stored in secure, HttpOnly, SameSite cookies with refresh token rotation and MFA one-time tokens.
                        - **Native API Versioning**: Spring Framework 7 header-driven API versioning (`X-API-Version: v1`, defaults to `v1`).
                        - **Enterprise Auditability**: Automatic entity auditing capturing `created_at`, `updated_at`, `created_by`, and `updated_by`.
                        - **Optimistic Concurrency**: Entity versioning (`@Version`) preventing lost updates on concurrent mutations.
                        - **Referential Integrity**: Cascading lifecycle management for project features, tech stacks, and blog tags.
                        """,
                contact = @Contact(
                        name = "Pranit Bhangale",
                        url = "https://pranitbhangale.vercel.app",
                        email = "pranitbhangale14@gmail.com"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                )
        ),
        servers = {
                @Server(
                        url = "http://localhost:8080",
                        description = "Local Development Environment"
                )
        },
        security = {
                @SecurityRequirement(name = "accessToken")
        }
)
@SecuritySchemes({
        @SecurityScheme(
                name = "accessToken",
                type = SecuritySchemeType.APIKEY,
                in = SecuritySchemeIn.COOKIE,
                paramName = "accessToken",
                description = "JWT access token stored in HttpOnly cookie"
        )
})
public class SwaggerConfig {
}
