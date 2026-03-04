package spring.ai.example.spring_ai_demo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatConfig {

    // ==========================================
    // 1. DEFINIZIONE DEI MODELLI (I "Motori")
    // ==========================================

    @Bean
    public OllamaChatModel llamaModel(OllamaApi ollamaApi) {
        return OllamaChatModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(OllamaChatOptions.builder()
                        .model("llama3.2")
                        .build())
                .build();
    }

    @Bean
    public OllamaChatModel mistralModel(OllamaApi ollamaApi) {
        return OllamaChatModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(OllamaChatOptions.builder()
                        .model("mistral")
                        .build())
                .build();
    }

    // ==========================================
    // 2. DEFINIZIONE DEI CLIENT (Le "Personalità")
    // ==========================================

    // Client 1: Il Llama standard (il tuo vecchio codice)
    @Bean
    public ChatClient llamaClient(OllamaChatModel llamaModel) {
        return ChatClient.builder(llamaModel).build();
    }

    // Client 2: Il Mistral standard (il tuo vecchio codice)
    @Bean
    public ChatClient mistralClient(OllamaChatModel mistralModel) {
        return ChatClient.builder(mistralModel).build();
    }

    // Client 3: Il Comico (basato su Llama)
    @Bean
    public ChatClient comedianClient(OllamaChatModel llamaModel) {
        return ChatClient.builder(llamaModel)
                .defaultSystem("Sei un comico simpatico. Rispondi a ogni domanda facendo una battuta divertente o un gioco di parole.")
                .build();
    }

    // Client 4: Il Professore (basato su Mistral, giusto per mixare un po'!)
    @Bean
    public ChatClient teacherClient(OllamaChatModel mistralModel) {
        return ChatClient.builder(mistralModel)
                .defaultSystem("Sei un severo e noioso professore universitario. Rispondi in modo estremamente formale, accademico e pedante.")
                .build();
    }
}