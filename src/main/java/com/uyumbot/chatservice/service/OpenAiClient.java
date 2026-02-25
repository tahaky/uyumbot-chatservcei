package com.uyumbot.chatservice.service;

import com.uyumbot.chatservice.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAiClient {

    private final WebClient webClient;

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.model:gpt-4o-mini}")
    private String model;

    @Value("${openai.base-url:https://api.openai.com}")
    private String baseUrl;

    private static final String SYSTEM_PROMPT_TEMPLATE =
            "You are a helpful assistant. Answer the user's question using ONLY the information " +
            "provided below. If the information does not contain the answer, say that you don't " +
            "have enough information to answer.\n\n" +
            "--- CONTEXT ---\n%s\n--- END CONTEXT ---";

    /**
     * Calls the OpenAI Chat Completions API with the conversation history and document context.
     * The model is instructed to answer ONLY based on the provided document search results.
     */
    @SuppressWarnings("unchecked")
    public String complete(List<ChatMessage> history, String documentContext) {
        List<Map<String, String>> messages = new ArrayList<>();

        String systemContent = String.format(SYSTEM_PROMPT_TEMPLATE, documentContext);
        messages.add(Map.of("role", "system", "content", systemContent));

        for (ChatMessage msg : history) {
            messages.add(Map.of("role", msg.getRole(), "content", msg.getContent()));
        }

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", messages
        );

        log.debug("Calling OpenAI with model={}, messages={}", model, messages.size());

        Map<String, Object> response = webClient.post()
                .uri(baseUrl + "/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null) {
            throw new IllegalStateException("Empty (null) response received from OpenAI API");
        }

        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        if (choices == null || choices.isEmpty()) {
            throw new IllegalStateException("No choices in OpenAI response: " + response);
        }

        Map<String, Object> messageObj = (Map<String, Object>) choices.get(0).get("message");
        if (messageObj == null) {
            throw new IllegalStateException("Missing 'message' field in OpenAI choice: " + choices.get(0));
        }
        return (String) messageObj.get("content");
    }
}
