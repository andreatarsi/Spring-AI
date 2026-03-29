package spring.ai.example.spring_ai_demo.web.dto;

public record ChatRequestDTO(
        String message,
        String conversationId, // <-- NUOVO: Identificativo univoco della chat/utente
        String imageUrl, // <-- NUOVO: L'URL dell'immagine da analizzare!
        String systemPrompt ,
        Boolean webSearch,    // <--- NUOVO: Accende la ricerca nativa (Gemini)
        Boolean deepThinking,  // <--- NUOVO: Accende il ragionamento (Claude)
        String model  // 🌟 IL NUOVO TELECOMANDO 🌟
) {}