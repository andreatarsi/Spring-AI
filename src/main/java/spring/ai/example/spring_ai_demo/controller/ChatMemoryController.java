package spring.ai.example.spring_ai_demo.controller;

import org.springframework.web.bind.annotation.*;
import spring.ai.example.spring_ai_demo.service.ChatMemoryService;
import java.util.Map;

@RestController
@RequestMapping("/ai/memory-window")
public class ChatMemoryController {

    private final ChatMemoryService chatMemoryService;

    public ChatMemoryController(ChatMemoryService chatMemoryService) {
        this.chatMemoryService = chatMemoryService;
    }

    @GetMapping("/chat")
    public Map<String, String> chat(
            @RequestParam String conversationId,
            @RequestParam String message,
            @RequestParam(defaultValue = "false") boolean remember) {

        String response = chatMemoryService.chat(conversationId, message, remember);

        return Map.of(
                "conversationId", conversationId,
                "mode", remember ? "Persistent (Database)" : "Volatile (RAM)",
                "output", response
        );
    }

    // Aggiungi questo endpoint nel tuo ChatMemoryController.java
    @DeleteMapping("/clear")
    public Map<String, String> clear(
            @RequestParam String conversationId,
            @RequestParam(defaultValue = "false") boolean remember) {

        chatMemoryService.clear(conversationId, remember);

        return Map.of(
                "message", "Memoria cancellata con successo",
                "conversationId", conversationId,
                "mode", remember ? "Persistent" : "Volatile"
        );
    }
}