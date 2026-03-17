package spring.ai.example.spring_ai_demo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaChatOptions;

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
    @Primary // Diciamo a Spring che questo è il modello "default" da iniettare quando serve un OllamaChatModel
    public OllamaChatModel mistralModel(OllamaApi ollamaApi) {
        return OllamaChatModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(OllamaChatOptions.builder()
                        .model("mistral")
                        .build())
                .build();
    }

    // Aggiungendo ("visionModel"), blindiamo il nome per il Qualifier!
    @Bean("visionModel")
    public ChatModel visionModel(OllamaApi ollamaApi) {
        return OllamaChatModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(OllamaChatOptions.builder()
                        .model("llama3.2-vision") // Il modello che ha letto la tua ricetta
                        .temperature(0.0)
                        .build())
                .build();
    }

    // 2. Il ChatClient dedicato alla vista
    @Bean
    @Qualifier("visionClient")
    public ChatClient visionChatClient(ChatModel llavaModel) {
        return ChatClient.builder(llavaModel).build();
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

    // Client 5: Il client "Tracciato" (Audited) con metadata di default
    @Bean
    public ChatClient auditedClient(OllamaChatModel llamaModel) {
        return ChatClient.builder(llamaModel)
                .defaultSystem(s -> s.text("Sei un assistente aziendale molto formale.")
                        .metadata("app-version", "1.0.0")
                        .metadata("department", "customer-service"))
                .defaultUser(u -> u.text("Richiesta di supporto standard.")
                        .metadata("default-priority", "low"))
                .build();
    }

    // Client 6: Il client "Camaleonte" con System Text parametrizzato e Opzioni di default
    @Bean
    public ChatClient dynamicRoleClient(OllamaChatModel llamaModel) {
        return ChatClient.builder(llamaModel)
                // Usiamo un placeholder {voice} nel system prompt di default
                .defaultSystem("Sei un assistente virtuale. Rispondi sempre immedesimandoti nella voce e nello stile di: {voice}")

                // Impostiamo delle opzioni di base per questo client (es. creatività alta)
                .defaultOptions(OllamaChatOptions.builder()
                        .temperature(0.8) // Più il valore è vicino a 1, più è creativo
                        .build())
                .build();
    }
}