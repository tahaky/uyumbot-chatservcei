package com.uyumbot.chatservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Response after sending a message")
public class SendMessageResponse {

    @Schema(description = "The assistant's reply based on document search results")
    private String reply;
}
