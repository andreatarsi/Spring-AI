package spring.ai.example.spring_ai_demo.web.dto;

import java.util.List;

public record ChatResponseDTO(
        Object answer, // Usiamo Object così può contenere sia una Stringa che un JSON
        String modelUsed,
        Integer promptTokens,
        Integer generationTokens,
        Integer totalTokens,
        List<String> toolsUsed // Lista dei tool eseguiti dall'IA
) {}