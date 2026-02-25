package com.uyumbot.chatservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Response after creating a new chat session")
public class CreateSessionResponse {

    @Schema(description = "The unique identifier of the newly created chat session")
    private String sessionId;
}
