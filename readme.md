# Uyumbot Chat Service

A Java 17 Spring Boot chat service with session-based history backed by MongoDB, document-grounded answers via OpenAI, and Swagger UI.

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/chat/sessions` | Create a new chat session. Returns `{ "sessionId": "..." }` |
| `POST` | `/chat/sessions/{sessionId}/messages` | Send a message. Returns `{ "reply": "..." }` |

Swagger UI is available at `http://localhost:8080/swagger-ui.html`.

## How It Works

1. A user creates a session (`POST /chat/sessions`).
2. For each message (`POST /chat/sessions/{sessionId}/messages`):
   - Session history is loaded from MongoDB.
   - The user message is sent to the **docservice** `POST /documents/search` endpoint to retrieve relevant document snippets.
   - OpenAI's Chat Completions API is called with the conversation history **and** the retrieved snippets as context.
   - The model is instructed to answer **only** based on the provided document context.
   - Both the user message and the assistant reply are saved back to MongoDB.

## Configuration

| Variable | Default | Description |
|----------|---------|-------------|
| `MONGODB_URI` | `mongodb://localhost:27017/chatdb` | MongoDB connection string |
| `DOCSERVICE_BASE_URL` | `http://localhost:8081` | Base URL of the doc service |
| `OPENAI_API_KEY` | *(required)* | OpenAI API key |
| `OPENAI_MODEL` | `gpt-4o-mini` | OpenAI model to use |
| `SERVER_PORT` | `8080` | HTTP port |

## Running Locally

```bash
# Start MongoDB + chatservice with Docker Compose
OPENAI_API_KEY=sk-... docker compose up --build
```

Or run the Spring Boot app directly (requires a running MongoDB):

```bash
export OPENAI_API_KEY=sk-...
./mvnw spring-boot:run
```

## Running Tests

```bash
./mvnw test
```
