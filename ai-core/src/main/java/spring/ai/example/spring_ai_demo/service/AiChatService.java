package spring.ai.example.spring_ai_demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;
import spring.ai.example.spring_ai_demo.web.ai.config.AiFeatureProperties;
import spring.ai.example.spring_ai_demo.web.dto.ChatRequestDTO;
import spring.ai.example.spring_ai_demo.web.dto.ChatResponseDTO;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AiChatService {

    private static final Logger log = LoggerFactory.getLogger(AiChatService.class);

    // Ora abbiamo una Mappa di tutti i client disponibili!
    private final Map<String, ChatClient> chatClients;

    private final AiFeatureProperties aiProperties;
    private final ChatMemory chatMemory;

    @Autowired
    public AiChatService(Map<String, ChatClient> chatClients, AiFeatureProperties aiProperties, ChatMemory chatMemory) {
        this.chatClients = chatClients;
        this.aiProperties = aiProperties;
        this.chatMemory = chatMemory;
    }

    public record StructuredResponse(String answer, String mood, String category) {}

    // =========================================================================
    // METODO HELPER: Restituisce il client corretto e prepara la richiesta
    // =========================================================================
    private ChatClient.ChatClientRequestSpec buildPromptSpec(ChatRequestDTO request, BeanOutputConverter<StructuredResponse> converter) {

        // --- SELEZIONE DINAMICA DEL MOTORE TRAMITE PAYLOAD ---
        // Leggiamo il modello richiesto dal DTO. Se è null, usiamo "gemini" come fallback.
        String requestedModel = (request.model() != null && !request.model().isBlank())
                ? request.model().toLowerCase()
                : "gemini";

        // Mappiamo il nome "amichevole" alla classe esatta presente nella nostra Map
        String activeEngine = switch (requestedModel) {
            case "openai", "gpt", "gpt-4o" -> "OpenAiChatModel";
            case "claude", "anthropic" -> "AnthropicChatModel";
            case "gemini", "google" -> "GoogleGenAiChatModel";
            default -> "GoogleGenAiChatModel"; // Fallback di sicurezza
        };

        ChatClient activeClient = chatClients.get(activeEngine);

        if (activeClient == null) {
            throw new RuntimeException("Motore AI non trovato o disabilitato: " + activeEngine + ". Modelli attualmente caricati: " + chatClients.keySet());
        }

        log.info("🚀 Payload richiede '{}' -> Preparazione prompt sul motore: {}", requestedModel, activeEngine);

        // Prepariamo il System Prompt partendo da quello richiesto dall'utente (se c'è)
        String finalSystemPrompt = (request.systemPrompt() != null && !request.systemPrompt().isBlank())
                ? request.systemPrompt()
                : "";

        // 1. FORZATURA JSON (Ora vive nel System Prompt, dove è sicuro!)
        if (converter != null && aiProperties.getFeatures().getOutputFormat() == AiFeatureProperties.OutputFormat.JSON) {
            finalSystemPrompt += "\n\n[DIRETTIVA DI SISTEMA]: Devi rispondere ESCLUSIVAMENTE fornendo un JSON valido. Non includere MAI preamboli, saluti o spiegazioni del tuo ragionamento. Usa rigorosamente questo schema:\n" + converter.getFormat();
        }

        // Usiamo il client selezionato dinamicamente!
        var promptSpec = activeClient.prompt();

        // 2. IMPOSTAZIONE SYSTEM PROMPT
        if (!finalSystemPrompt.isBlank()) {
            promptSpec.system(finalSystemPrompt);
        }

        // Il messaggio dell'utente ora rimane pulito e innocente!
        String testoSicuro = request.message();

        // 3. VISIONE (Immagine opzionale)
        if (request.imageUrl() != null && !request.imageUrl().isBlank()) {
            promptSpec.user(u -> {
                try {
                    u.text(testoSicuro)
                            .media(MimeTypeUtils.IMAGE_JPEG, new UrlResource(request.imageUrl()));
                } catch (MalformedURLException e) {
                    log.error("URL immagine non valido!", e);
                    u.text(testoSicuro); // Fallback: mandiamo solo il testo
                }
            });
        } else {
            promptSpec.user(testoSicuro); // Flusso normale senza immagini
        }

        // 4. MEMORIA MULTI-UTENTE
        if (aiProperties.getFeatures().isMemoryEnabled() && request.conversationId() != null) {
            promptSpec.advisors(MessageChatMemoryAdvisor.builder(chatMemory)
                    .conversationId(request.conversationId())
                    .build());
        }

        return promptSpec;
    }

    // =========================================================================
    // ENDPOINT 1: RISPOSTA SINCRONA
    // =========================================================================
    public ChatResponseDTO chat(ChatRequestDTO request) {
        log.info("Richiesta Sync in arrivo da [{}]: {}", request.conversationId(), request.message());

        BeanOutputConverter<StructuredResponse> converter = new BeanOutputConverter<>(StructuredResponse.class);
        var promptSpec = buildPromptSpec(request, converter);

        ChatResponse response = promptSpec.call().chatResponse();
        String rawContent = response.getResult().getOutput().getText();
        Object finalAnswer = rawContent;

        if (aiProperties.getFeatures().getOutputFormat() == AiFeatureProperties.OutputFormat.JSON) {
            try {
                finalAnswer = converter.convert(rawContent);
            } catch (Exception e) {
                log.error("L'IA ha sbagliato a formattare il JSON! Contenuto: {}", rawContent);
                finalAnswer = rawContent;
            }
        }

        Usage usage = response.getMetadata().getUsage();
        int promptTokens = usage != null ? usage.getPromptTokens() : 0;
        int genTokens = usage != null ? usage.getCompletionTokens() : 0;
        int totalTokens = usage != null ? usage.getTotalTokens() : 0;

        List<String> executedTools = new ArrayList<>();
        if (response.getResult().getOutput().hasToolCalls()) {
            executedTools = response.getResult().getOutput().getToolCalls().stream()
                    .map(toolCall -> toolCall.name()).toList();
        }

        return new ChatResponseDTO(finalAnswer, response.getMetadata().getModel(), promptTokens, genTokens, totalTokens, executedTools);
    }

    // =========================================================================
    // ENDPOINT 2: STREAMING REATTIVO CON METADATI
    // =========================================================================
    public Flux<String> streamChat(ChatRequestDTO request) {
        log.info("🌊 Richiesta Stream in arrivo da [{}]: {}", request.conversationId(), request.message());

        // Niente JSON per lo streaming, passiamo null al converter
        var promptSpec = buildPromptSpec(request, null);

        // Invece di .content(), chiediamo l'intera .chatResponse() in streaming!
        return promptSpec.stream().chatResponse()
                .map(response -> {
                    // Estraiamo il pezzettino di testo (se c'è)
                    String chunk = (response.getResult() != null && response.getResult().getOutput().getText() != null)
                            ? response.getResult().getOutput().getText()
                            : "";

                    // Quando il flusso sta per finire, Spring AI inietta i metadati!
                    if (response.getMetadata() != null && response.getMetadata().getUsage() != null) {
                        var usage = response.getMetadata().getUsage();

                        // Se i token totali sono > 0, significa che è il pacchetto finale
                        if (usage.getTotalTokens() > 0) {
                            String metadatiFinali = String.format(
                                    "\n\n📊 [METADATI STREAMING] Modello: %s | Prompt Tokens: %d | Generati: %d | Totali: %d",
                                    response.getMetadata().getModel(),
                                    usage.getPromptTokens(),
                                    usage.getCompletionTokens(),
                                    usage.getTotalTokens()
                            );
                            // Appiccichiamo i metadati in fondo alla stringa
                            return chunk + metadatiFinali;
                        }
                    }
                    return chunk;
                });
    }
}