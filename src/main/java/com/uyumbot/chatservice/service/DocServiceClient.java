package com.uyumbot.chatservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocServiceClient {

    private final WebClient webClient;

    @Value("${docservice.base-url}")
    private String docServiceBaseUrl;

    /**
     * Calls the docservice /documents/search endpoint and returns the found document snippets
     * as a single concatenated string suitable for use as context in a prompt.
     */
    @SuppressWarnings("unchecked")
    public String searchDocuments(String query) {
        log.debug("Searching documents for query: {}", query);

        Map<String, String> body = Map.of("query", query);

        List<Map<String, Object>> results;
        try {
            results = webClient.post()
                    .uri(docServiceBaseUrl + "/documents/search")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(List.class)
                    .block();
        } catch (Exception e) {
            log.error("DocService call failed [{}]: {}", e.getClass().getSimpleName(), e.getMessage());
            return "";
        }

        if (results == null || results.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (Map<String, Object> doc : results) {
            Object content = doc.get("content");
            if (content != null) {
                sb.append(content).append("\n\n");
            }
        }
        return sb.toString().trim();
    }
}
