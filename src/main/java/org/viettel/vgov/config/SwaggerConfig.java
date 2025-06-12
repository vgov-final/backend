package org.viettel.vgov.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("VGov Project Management API")
                        .version("1.0.0")
                        .description("API documentation for VGov Project Management System - A comprehensive project and employee management platform"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT token authentication")))
                .tags(Arrays.asList(
                        new Tag().name("Authentication").description("User authentication and authorization endpoints"),
                        new Tag().name("User Management").description("User CRUD operations (Admin only)"),
                        new Tag().name("Profile Management").description("User profile and personal settings"),
                        new Tag().name("Project Management").description("Project CRUD operations and status management"),
                        new Tag().name("Project Members").description("Project member assignment and workload management"),
                        new Tag().name("Work Logs").description("Work log tracking and reporting"),
                        new Tag().name("Dashboard & Analytics").description("Dashboard overview and analytics data"),
                        new Tag().name("Notifications").description("In-app notification management"),
                        new Tag().name("System & Lookup").description("System information and lookup data endpoints")
                ));
    }
}
