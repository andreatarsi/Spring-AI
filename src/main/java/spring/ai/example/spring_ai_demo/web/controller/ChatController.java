package spring.ai.example.spring_ai_demo.web.controller;

import org.springframework.web.bind.annotation.*;
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

    // 1. Endpoint RESTful standard (POST per i frontend)
    @PostMapping
    public ChatResponseDTO chat(@RequestBody ChatRequestDTO request) {
        return aiChatService.chat(request);
    }

    // 2. Endpoint rapido per test testuale dal browser (GET)
    @GetMapping("/quick")
    public ChatResponseDTO quickChat(
            @RequestParam(defaultValue = "Ciao!") String message,
            @RequestParam(defaultValue = "guest_session") String chatId) { // <-- NUOVO
        return aiChatService.chat(new ChatRequestDTO(message, null, chatId));
    }

    // 3. L'Endpoint Strutturato (Aggiornato!)
    // Nota: Ora fa la stessa identica cosa del /quick, perché il formato
    // viene deciso dal file application.yml (output-format: JSON/TEXT)
    @GetMapping("/structured")
    public ChatResponseDTO getStructuredResponse(
            @RequestParam String message,
            @RequestParam(defaultValue = "guest_session") String chatId) { // <-- NUOVO
        return aiChatService.chat(new ChatRequestDTO(message, null, chatId));
    }
}