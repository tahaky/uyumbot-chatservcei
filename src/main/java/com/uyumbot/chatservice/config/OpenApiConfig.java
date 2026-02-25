package com.uyumbot.chatservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI chatServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Uyumbot Chat Service API")
                        .version("1.0.0")
                        .description("Session-based chat service backed by MongoDB and OpenAI, " +
                                     "grounded in document search results from the doc service."));
    }
}
