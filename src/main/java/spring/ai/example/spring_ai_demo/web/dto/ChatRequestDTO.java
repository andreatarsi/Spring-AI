package spring.ai.example.spring_ai_demo.web.dto;

public record ChatRequestDTO(
        String message,
        String systemPrompt, // Opzionale, se vogliamo sovrascrivere il comportamento
        String conversationId // <-- NUOVO: Identificativo univoco della chat/utente
) {}