package com.uyumbot.chatservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request body for sending a user message")
public class SendMessageRequest {

    @NotBlank
    @Schema(description = "The user's message text", example = "What is the refund policy?")
    private String message;
}
