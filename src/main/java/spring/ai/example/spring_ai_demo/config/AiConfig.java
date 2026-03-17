package spring.ai.example.spring_ai_demo.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AiConfig {

    // Stiamo sovrascrivendo il ChatClient di default di Spring Boot
    @Bean
    @Primary
    public ChatClient customChatClient(ChatClient.Builder builder) {

        System.out.println("⚙️ [AiConfig] Inizializzazione ChatClient Globale...");

        return builder
                // 1. Diciamo che di default vogliamo SEMPRE forzare il formato strutturato nativo!
                .defaultAdvisors(a -> a.param("ENABLE_NATIVE_STRUCTURED_OUTPUT", true))
                // 2. Possiamo anche impostare un System Prompt di default per tutta l'app!
                .defaultSystem("Sei un assistente virtuale aziendale molto preciso e sintetico.")
                .build();
    }
}