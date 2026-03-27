package spring.ai.example.spring_ai_demo.web.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import spring.ai.example.spring_ai_demo.service.AiChatService;
import spring.ai.example.spring_ai_demo.web.dto.ChatRequestDTO;
import spring.ai.example.spring_ai_demo.web.dto.ChatResponseDTO;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final AiChatService aiChatService;

    public ChatController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    // Endpoint RESTful standard (POST per i frontend)
    @PostMapping
    public ChatResponseDTO chat(@RequestBody ChatRequestDTO request) {
        return aiChatService.chat(request);
    }

    // Endpoint rapido per test testuale dal browser (GET)
    @GetMapping("/quick")
    public ChatResponseDTO quickChat(
            @RequestParam(defaultValue = "Ciao!") String message,
            @RequestParam(defaultValue = "guest_session") String chatId,
            @RequestParam(required = false) String imageUrl,
            @RequestParam(required = false) String systemPrompt,
            @RequestParam(required = false) Boolean webSearch,      // <-- AGGIUNTO
            @RequestParam(required = false) Boolean deepThinking) { // <-- AGGIUNTO

        return aiChatService.chat(new ChatRequestDTO(message, chatId, systemPrompt, imageUrl, webSearch, deepThinking));
    }

    // Endpoint Strutturato
    @GetMapping("/structured")
    public ChatResponseDTO getStructuredResponse(
            @RequestParam String message,
            @RequestParam(defaultValue = "guest_session") String chatId,
            @RequestParam(required = false) String imageUrl,
            @RequestParam(required = false) String systemPrompt,
            @RequestParam(required = false) Boolean webSearch,      // <-- AGGIUNTO
            @RequestParam(required = false) Boolean deepThinking) { // <-- AGGIUNTO

        return aiChatService.chat(new ChatRequestDTO(message, chatId, systemPrompt, imageUrl, webSearch, deepThinking));
    }

    // Endpoint STREAMING (Server-Sent Events)
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(
            @RequestParam(defaultValue = "Raccontami una breve storia.") String message,
            @RequestParam(defaultValue = "guest_session") String chatId,
            @RequestParam(required = false) String imageUrl,
            @RequestParam(required = false) String systemPrompt,
            @RequestParam(required = false) Boolean webSearch,      // <-- AGGIUNTO
            @RequestParam(required = false) Boolean deepThinking) { // <-- AGGIUNTO

        return aiChatService.streamChat(new ChatRequestDTO(message, chatId, systemPrompt, imageUrl, webSearch, deepThinking));
    }
}