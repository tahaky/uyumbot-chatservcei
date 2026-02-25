package com.uyumbot.chatservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uyumbot.chatservice.controller.ChatController;
import com.uyumbot.chatservice.model.ChatSession;
import com.uyumbot.chatservice.model.SendMessageRequest;
import com.uyumbot.chatservice.service.ChatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatController.class)
class ChatControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ChatService chatService;

    @Test
    void createSession_returns201WithSessionId() throws Exception {
        ChatSession session = new ChatSession();
        session.setId("test-session-id");
        when(chatService.createSession()).thenReturn(session);

        mockMvc.perform(post("/chat/sessions"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sessionId").value("test-session-id"));
    }

    @Test
    void sendMessage_returnsReply() throws Exception {
        when(chatService.sendMessage(anyString(), anyString()))
                .thenReturn("Based on the documents, the answer is X.");

        SendMessageRequest request = new SendMessageRequest();
        request.setMessage("What is the refund policy?");

        mockMvc.perform(post("/chat/sessions/test-session-id/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply").value("Based on the documents, the answer is X."));
    }

    @Test
    void sendMessage_blankMessage_returns400() throws Exception {
        SendMessageRequest request = new SendMessageRequest();
        request.setMessage("");

        mockMvc.perform(post("/chat/sessions/test-session-id/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
