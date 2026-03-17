package spring.ai.example.spring_ai_demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatOptions;
import org.springframework.ai.vertexai.gemini.common.VertexAiGeminiSafetyRating;
import org.springframework.ai.vertexai.gemini.common.VertexAiGeminiSafetySetting;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ai/vertex")
public class VertexController {

    private static final Logger logger = LoggerFactory.getLogger(VertexController.class);
    private final ChatClient chatClient;

    public VertexController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * SUPERPOTERE ENTERPRISE: Safety Settings & Ratings
     * Blocca i contenuti pericolosi e genera un report di sicurezza per l'audit aziendale.
     */
    @GetMapping("/sicurezza-aziendale")
    public String auditDiSicurezza() {
        logger.info("🛡️ Avvio Vertex AI con filtri di Sicurezza Massima...");

        // 1. Definiamo regole di sicurezza rigidissime
        List<VertexAiGeminiSafetySetting> filtriSicurezza = List.of(
                VertexAiGeminiSafetySetting.builder()
                        .withCategory(VertexAiGeminiSafetySetting.HarmCategory.HARM_CATEGORY_HATE_SPEECH)
                        // Blocca anche se c'è una probabilità "Bassa" di discorso d'odio
                        .withThreshold(VertexAiGeminiSafetySetting.HarmBlockThreshold.BLOCK_LOW_AND_ABOVE)
                        .build(),
                VertexAiGeminiSafetySetting.builder()
                        .withCategory(VertexAiGeminiSafetySetting.HarmCategory.HARM_CATEGORY_DANGEROUS_CONTENT)
                        .withThreshold(VertexAiGeminiSafetySetting.HarmBlockThreshold.BLOCK_LOW_AND_ABOVE)
                        .build()
        );

        // 2. Facciamo la chiamata (chiedendo l'intero oggetto ChatResponse)
        ChatResponse response = chatClient.prompt()
                .user("Scrivi un testo controverso su argomenti politici sensibili.")
                .options(VertexAiGeminiChatOptions.builder()
                        .model("gemini-2.0-flash")
                        .safetySettings(filtriSicurezza) // Applichiamo i filtri!
                        .build())
                .call()
                .chatResponse();

        // 3. Estraiamo il Report di Sicurezza (Safety Ratings) per i log di Audit
        StringBuilder report = new StringBuilder("\n\n🛡️ REPORT DI SICUREZZA VERTEX AI:\n");

        @SuppressWarnings("unchecked")
        List<VertexAiGeminiSafetyRating> safetyRatings = (List<VertexAiGeminiSafetyRating>) response.getResult()
                .getOutput()
                .getMetadata()
                .get("safetyRatings");

        if (safetyRatings != null) {
            for (VertexAiGeminiSafetyRating rating : safetyRatings) {
                report.append(String.format("- Categoria: %s | Probabilità: %s | Bloccato: %b\n",
                        rating.category(), rating.probability(), rating.blocked()));
            }
        }

        return "Risposta (se non bloccata): " + response.getResult().getOutput().getText() + report.toString();
    }

    /**
     * SUPERPOTERE ENTERPRISE 2: Output Strutturato Garantito (JSON Schema)
     * Perfetto per estrarre dati da testi non strutturati (es. email, curriculum)
     * e trasformarli in oggetti Java sicuri.
     */
    @GetMapping("/estrai-dati")
    public String estraiDati() {
        logger.info("📄 Avvio Vertex AI con Output Strutturato (JSON Schema)...");

        // 1. Definiamo lo schema esatto che vogliamo indietro (Formato OpenAPI)
        String jsonSchema = """
                {
                  "type": "object",
                  "properties": {
                    "nome": { "type": "string" },
                    "cognome": { "type": "string" },
                    "eta": { "type": "integer" },
                    "professione": { "type": "string" }
                  },
                  "required": ["nome", "cognome", "eta", "professione"]
                }
                """;

        // 2. Facciamo analizzare un testo caotico all'IA
        return chatClient.prompt()
                .user("Estrai i dati da questo blocco di testo disordinato: 'Ieri ho incontrato il Dott. Mario Rossi. Mi ha detto che a 45 anni fare l'ingegnere aerospaziale è faticoso ma bello.'")
                .options(VertexAiGeminiChatOptions.builder()
                        .model("gemini-2.0-flash")
                        // Diciamo all'API che non vogliamo testo, ma un JSON puro!
                        .responseMimeType("application/json")
                        // Imponiamo il nostro schema di validazione
                        .responseSchema(jsonSchema)
                        .build())
                .call()
                .content();
    }
}