package spring.ai.example.spring_ai_demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;
import spring.ai.example.spring_ai_demo.web.ai.config.AiFeatureProperties;
import spring.ai.example.spring_ai_demo.web.dto.ChatRequestDTO;
import spring.ai.example.spring_ai_demo.web.dto.ChatResponseDTO;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.support.ToolCallbacks;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class AiChatService {

    private static final Logger log = LoggerFactory.getLogger(AiChatService.class);

    private final Map<String, ChatClient> chatClients;
    private final AiFeatureProperties aiProperties;
    private final ChatMemory chatMemory;

    // Il nostro servizio meteo nativo
    private final WeatherMcpService weatherService;

    private final List<ToolCallback> mcpTools = new ArrayList<>();

    @Autowired
    public AiChatService(Map<String, ChatClient> chatClients,
                         AiFeatureProperties aiProperties,
                         ChatMemory chatMemory,
                         WeatherMcpService weatherService, // <-- INIETTATO QUI!
                         @Autowired(required = false) List<ToolCallbackProvider> toolProviders) {

        this.chatClients = chatClients;
        this.aiProperties = aiProperties;
        this.chatMemory = chatMemory;
        this.weatherService = weatherService;

        // Spacchettiamo i tools MCP esterni e applichiamo il DECORATOR
        if (toolProviders != null) {
            for (ToolCallbackProvider provider : toolProviders) {
                for (ToolCallback originalTool : provider.getToolCallbacks()) {
                    ToolCallback trackedTool = new ToolCallback() {
                        @Override
                        public ToolDefinition getToolDefinition() {
                            return originalTool.getToolDefinition();
                        }

                        @Override
                        public String call(String toolCallRequest) {
                            String toolName = originalTool.getToolDefinition().name();
                            if (aiProperties.getFeatures().isDebugTools()) {
                                log.info("🧰 [DEBUG MCP] L'IA sta usando il tool: '{}'", toolName);
                                log.info("📥 [DEBUG MCP] Dati inviati dall'IA: {}", toolCallRequest);
                            }
                            String result = originalTool.call(toolCallRequest);
                            if (aiProperties.getFeatures().isDebugTools()) {
                                log.info("📤 [DEBUG MCP] Risposta del server tornata all'IA: {}", result);
                            }
                            return result;
                        }
                    };
                    this.mcpTools.add(trackedTool);
                }
            }
        }
    }

    public record StructuredResponse(String answer, String mood, String category) {}

    private ChatClient.ChatClientRequestSpec buildPromptSpec(ChatRequestDTO request, BeanOutputConverter<StructuredResponse> converter) {

        String requestedModel = (request.model() != null && !request.model().isBlank())
                ? request.model().toLowerCase()
                : "gemini";

        String activeEngine = switch (requestedModel) {
            case "openai", "gpt", "gpt-4o" -> "OpenAiChatModel";
            case "claude", "anthropic" -> "AnthropicChatModel";
            case "gemini", "google" -> "GoogleGenAiChatModel";
            case "ollama" -> "OllamaChatModel";
            default -> "GoogleGenAiChatModel";
        };

        ChatClient activeClient = chatClients.get(activeEngine);

        if (activeClient == null) {
            throw new RuntimeException("Motore AI non trovato o disabilitato: " + activeEngine);
        }

        log.info("🚀 Payload richiede '{}' -> Preparazione prompt sul motore: {}", requestedModel, activeEngine);

        String finalSystemPrompt = (request.systemPrompt() != null && !request.systemPrompt().isBlank())
                ? request.systemPrompt()
                : "";

        if (converter != null && aiProperties.getFeatures().getOutputFormat() == AiFeatureProperties.OutputFormat.JSON) {
            finalSystemPrompt += "\n\n[DIRETTIVA DI SISTEMA]: Devi rispondere ESCLUSIVAMENTE fornendo un JSON valido. Non includere MAI preamboli, saluti o spiegazioni del tuo ragionamento. Usa rigorosamente questo schema:\n" + converter.getFormat();
        }

        var promptSpec = activeClient.prompt();

        if (!finalSystemPrompt.isBlank()) {
            promptSpec.system(finalSystemPrompt);
        }

        String testoSicuro = request.message();

        if (request.imageUrl() != null && !request.imageUrl().isBlank()) {
            promptSpec.user(u -> {
                try {
                    u.text(testoSicuro)
                            .media(MimeTypeUtils.IMAGE_JPEG, new UrlResource(request.imageUrl()));
                } catch (MalformedURLException e) {
                    log.error("URL immagine non valido!", e);
                    u.text(testoSicuro);
                }
            });
        } else {
            promptSpec.user(testoSicuro);
        }

        if (aiProperties.getFeatures().isMemoryEnabled() && request.conversationId() != null) {
            promptSpec.advisors(MessageChatMemoryAdvisor.builder(chatMemory)
                    .conversationId(request.conversationId())
                    .build());
        }

        // 🌟 IL MOMENTO DELLA VERITÀ: Uniamo gli strumenti esterni a quelli interni!
        if (aiProperties.getFeatures().isToolsEnabled()) {

            // 1. Partiamo dai Tool MCP (già impacchettati nel nostro Decorator)
            List<ToolCallback> allToolCallbacks = new ArrayList<>(this.mcpTools);

            // 2. Aggiungiamo il servizio meteo locale
            if (this.weatherService != null) {
                // Traduciamo al volo il POJO Java in ToolCallback standard
                ToolCallback[] localTools = ToolCallbacks.from(this.weatherService);
                allToolCallbacks.addAll(Arrays.asList(localTools));
            }

            log.info("🛠️ Passando {} strumenti complessivi al LLM...", allToolCallbacks.size());

            // 3. Usiamo il metodo corretto per le interfacce native
            promptSpec.toolCallbacks(allToolCallbacks.toArray(new ToolCallback[0]));
        }

        return promptSpec;
    }

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

        return new ChatResponseDTO(finalAnswer, response.getMetadata().getModel(), promptTokens, genTokens, totalTokens, new ArrayList<>());
    }

    public Flux<String> streamChat(ChatRequestDTO request) {
        log.info("🌊 Richiesta Stream in arrivo da [{}]: {}", request.conversationId(), request.message());

        var promptSpec = buildPromptSpec(request, null);

        return promptSpec.stream().chatResponse()
                .map(response -> {
                    String chunk = (response.getResult() != null && response.getResult().getOutput().getText() != null)
                            ? response.getResult().getOutput().getText()
                            : "";

                    if (response.getMetadata() != null && response.getMetadata().getUsage() != null) {
                        var usage = response.getMetadata().getUsage();
                        if (usage.getTotalTokens() > 0) {
                            String metadatiFinali = String.format(
                                    "\n\n📊 [METADATI STREAMING] Modello: %s | Prompt Tokens: %d | Generati: %d | Totali: %d",
                                    response.getMetadata().getModel(),
                                    usage.getPromptTokens(),
                                    usage.getCompletionTokens(),
                                    usage.getTotalTokens()
                            );
                            return chunk + metadatiFinali;
                        }
                    }
                    return chunk;
                });
    }
}