package backup.controller_backup;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai/memory")
public class MemoryController {

    private final ChatClient chatClient;

    // 1. Creiamo il "Database" in RAM
    private final InMemoryChatMemoryRepository repository = new InMemoryChatMemoryRepository();

    // 2. Usiamo il BUILDER vuoto e concateniamo il repository!
    private final ChatMemory chatMemory = MessageWindowChatMemory.builder()
            .chatMemoryRepository(repository) // <-- La novità è qui!
            .maxMessages(20)
            .build();

    public MemoryController(@Qualifier("llamaClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/chat")
    public String chatWithMemory(
            @RequestParam(defaultValue = "utente-1") String chatId,
            @RequestParam String message) {

        // 3. Usiamo il BUILDER anche per il MessageChatMemoryAdvisor (Best Practice)
        MessageChatMemoryAdvisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory)
                .conversationId(chatId)
                .build();

        return chatClient.prompt()
                .user(message)
                // Aggiungiamo l'Advisor costruito
                .advisors(memoryAdvisor)
                .call()
                .content();
    }
}