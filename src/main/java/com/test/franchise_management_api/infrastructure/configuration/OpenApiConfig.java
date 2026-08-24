package com.test.franchise_management_api.infrastructure.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI franchiseOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Franchise Management API")
                .description("Reactive API to manage franchises, branches and products")
                .version("v1")
                .contact(new Contact().name("Backend Team")));
    }
}

