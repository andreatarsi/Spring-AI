package spring.ai.example.spring_ai_demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Service;
import spring.ai.example.spring_ai_demo.ai.config.AiFeatureProperties;
import spring.ai.example.spring_ai_demo.web.dto.ChatRequestDTO;
import spring.ai.example.spring_ai_demo.web.dto.ChatResponseDTO;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;

import java.util.ArrayList;
import java.util.List;

@Service
public class AiChatService {

    private static final Logger log = LoggerFactory.getLogger(AiChatService.class);
    private final ChatClient chatClient;
    private final AiFeatureProperties aiProperties;
    private final ChatMemory chatMemory;

    public AiChatService(ChatClient chatClient, AiFeatureProperties aiProperties, ChatMemory chatMemory) {
        this.chatClient = chatClient;
        this.aiProperties = aiProperties;
        this.chatMemory = chatMemory;
    }

    // -------------------------------------------------------------------------
    // IL DTO INTERNO (Usato solo da Spring per forzare il formato JSON dell'IA)
    // -------------------------------------------------------------------------
    public record StructuredResponse(
            String answer,
            String mood,
            String category
    ) {}

    // -------------------------------------------------------------------------
    // L'UNICO METODO CHE TI SERVE (Gestisce tutto: Testo, JSON, Token, Tool, Memoria)
    // -------------------------------------------------------------------------
    public ChatResponseDTO chat(ChatRequestDTO request) {
        log.info("Richiesta DTO in arrivo da [{}]: {}", request.conversationId(), request.message());

        // 1. Prepariamo il convertitore per il JSON (se richiesto)
        BeanOutputConverter<StructuredResponse> converter = new BeanOutputConverter<>(StructuredResponse.class);
        String finalUserMessage = request.message();

        // 2. FORZATURA JSON: Aggiungiamo le regole in coda al messaggio dell'utente
        if (aiProperties.getFeatures().getOutputFormat() == AiFeatureProperties.OutputFormat.JSON) {
            finalUserMessage = finalUserMessage + "\n\nRISPONDI TASSATIVAMENTE USANDO QUESTO FORMATO JSON E NESSUN ALTRO TESTO:\n" + converter.getFormat();
        }

        // 3. Creiamo la chiamata con il messaggio potenziato
        var promptSpec = chatClient.prompt().user(finalUserMessage);

        // 4. MEMORIA MULTI-UTENTE: Agganciamo la memoria dinamicamente
        if (aiProperties.getFeatures().isMemoryEnabled() && request.conversationId() != null) {
            promptSpec.advisors(MessageChatMemoryAdvisor.builder(chatMemory)
                    .conversationId(request.conversationId())
                    .build());
        }

        // 5. Eseguiamo la chiamata chiedendo l'Oggetto Complesso (ChatResponse)
        ChatResponse response = promptSpec.call().chatResponse();

        // 6. Estrazione del testo nudo e crudo
        String rawContent = response.getResult().getOutput().getText();
        Object finalAnswer = rawContent;

        // 7. Tentativo di conversione: se volevamo JSON, lo mappiamo nel DTO interno
        if (aiProperties.getFeatures().getOutputFormat() == AiFeatureProperties.OutputFormat.JSON) {
            try {
                finalAnswer = converter.convert(rawContent);
            } catch (Exception e) {
                log.error("L'IA ha sbagliato a formattare il JSON! Contenuto grezzo: {}", rawContent);
                finalAnswer = rawContent; // Fallback al testo
            }
        }

        // 8. Estrazione Metriche (Token e Modello)
        String model = response.getMetadata().getModel();
        Usage usage = response.getMetadata().getUsage();
        int promptTokens = usage != null ? usage.getPromptTokens() : 0;
        int genTokens = usage != null ? usage.getCompletionTokens() : 0;
        int totalTokens = usage != null ? usage.getTotalTokens() : 0;

        // 9. Estrazione dei Tool Eseguiti
        List<String> executedTools = new ArrayList<>();
        if (response.getResult().getOutput().hasToolCalls()) {
            executedTools = response.getResult().getOutput().getToolCalls().stream()
                    .map(toolCall -> toolCall.name())
                    .toList();
        }

        // 10. Ritorno al Controller con la scatola nera completa!
        return new ChatResponseDTO(finalAnswer, model, promptTokens, genTokens, totalTokens, executedTools);
    }
}