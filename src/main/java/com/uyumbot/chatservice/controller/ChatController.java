package com.uyumbot.chatservice.controller;

import com.uyumbot.chatservice.model.CreateSessionResponse;
import com.uyumbot.chatservice.model.SendMessageRequest;
import com.uyumbot.chatservice.model.SendMessageResponse;
import com.uyumbot.chatservice.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Tag(name = "Chat", description = "Session-based chat endpoints")
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/sessions")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a new chat session",
            description = "Creates a new chat session and returns the session ID.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Session created successfully")
            }
    )
    public CreateSessionResponse createSession() {
        String sessionId = chatService.createSession().getId();
        return new CreateSessionResponse(sessionId);
    }

    @PostMapping("/sessions/{sessionId}/messages")
    @Operation(
            summary = "Send a message in a chat session",
            description = "Accepts a user message, searches relevant documents via the doc service, " +
                          "and returns an answer from OpenAI that is grounded ONLY in those documents.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Reply generated successfully"),
                    @ApiResponse(responseCode = "404", description = "Session not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid request body")
            }
    )
    public SendMessageResponse sendMessage(
            @Parameter(description = "The chat session ID", required = true)
            @PathVariable String sessionId,
            @Valid @RequestBody SendMessageRequest request) {
        String reply = chatService.sendMessage(sessionId, request.getMessage());
        return new SendMessageResponse(reply);
    }
}
