package com.uyumbot.chatservice.service;

import com.uyumbot.chatservice.model.ChatMessage;
import com.uyumbot.chatservice.model.ChatSession;
import com.uyumbot.chatservice.repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatSessionRepository sessionRepository;
    private final DocServiceClient docServiceClient;
    private final OpenAiClient openAiClient;

    /**
     * Creates a new chat session and persists it in MongoDB.
     *
     * @return the newly created {@link ChatSession}
     */
    public ChatSession createSession() {
        ChatSession session = ChatSession.builder()
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        ChatSession saved = sessionRepository.save(session);
        log.info("Created chat session: {}", saved.getId());
        return saved;
    }

    /**
     * Processes a user message within an existing session:
     * <ol>
     *   <li>Loads session history from MongoDB</li>
     *   <li>Appends the user message</li>
     *   <li>Calls docservice /documents/search to retrieve relevant context</li>
     *   <li>Calls OpenAI with the history + document context</li>
     *   <li>Appends the assistant reply and saves the session</li>
     * </ol>
     *
     * @param sessionId the session identifier
     * @param userMessage the text of the user message
     * @return the assistant's reply
     */
    public String sendMessage(String sessionId, String userMessage) {
        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                        "Chat session not found: " + sessionId));

        ChatMessage userMsg = ChatMessage.builder()
                .role("user")
                .content(userMessage)
                .timestamp(Instant.now())
                .build();
        session.getMessages().add(userMsg);

        String documentContext = docServiceClient.searchDocuments(userMessage);

        List<ChatMessage> history = session.getMessages();
        String reply = openAiClient.complete(history, documentContext);

        ChatMessage assistantMsg = ChatMessage.builder()
                .role("assistant")
                .content(reply)
                .timestamp(Instant.now())
                .build();
        session.getMessages().add(assistantMsg);
        session.setUpdatedAt(Instant.now());

        sessionRepository.save(session);
        log.info("Message processed for session {}", sessionId);
        return reply;
    }
}
