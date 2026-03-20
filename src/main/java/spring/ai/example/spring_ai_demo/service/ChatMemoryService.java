package spring.ai.example.spring_ai_demo.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;
@Service
public class ChatMemoryService {

    private final ChatClient persistentClient;
    private final ChatClient volatileClient;

    // Memorizziamo le istanze di ChatMemory, non i repository
    private final ChatMemory jdbcMemory;
    private final ChatMemory ramMemory;

    public ChatMemoryService(ChatModel chatModel, JdbcChatMemoryRepository jdbcRepo) {

        // 1. Inizializziamo la memoria persistente
        this.jdbcMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(jdbcRepo)
                .maxMessages(10)
                .build();

        this.persistentClient = ChatClient.builder(chatModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(jdbcMemory).build())
                .build();

        // 2. Inizializziamo la memoria volatile
        this.ramMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .maxMessages(10)
                .build();

        this.volatileClient = ChatClient.builder(chatModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(ramMemory).build())
                .build();
    }

    // Il metodo clear va chiamato sull'oggetto ChatMemory
    public void clear(String conversationId, boolean persistent) {
        if (persistent) {
            this.jdbcMemory.clear(conversationId); // Svuota il DB per questo ID
        } else {
            this.ramMemory.clear(conversationId);  // Svuota la RAM per questo ID
        }
    }

    public String chat(String conversationId, String message, boolean persistent) {
        ChatClient client = persistent ? persistentClient : volatileClient;
        return client.prompt()
                .user(message)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }
}