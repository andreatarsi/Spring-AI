package spring.ai.example.spring_ai_demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.mistralai.MistralAiChatOptions;
import org.springframework.ai.mistralai.ocr.MistralOcrApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ai.chat.model.ChatModel; // <-- Aggiungi questo
import org.springframework.beans.factory.annotation.Qualifier; // <-- Aggiungi questo

import java.net.MalformedURLException;
import java.util.List;

//@RestController
@RequestMapping("/ai/mistral")
public class MistralController {

    private static final Logger logger = LoggerFactory.getLogger(MistralController.class);
    private final ChatClient chatClient;
    private final String mistralApiKey;

    // Iniettiamo il ChatClient e ci "rubiamo" la chiave API dalle properties per l'OCR
    // REFACTORING: Uniamo il @Qualifier per il motore e il @Value per la stringa
    public MistralController(
            @Qualifier("mistralAiChatModel") ChatModel chatModel,
            @Value("${spring.ai.mistralai.api-key:dummy}") String mistralApiKey) {

        // 1. Costruiamo il client blindato su Mistral
        this.chatClient = ChatClient.create(chatModel);

        // 2. Salviamo la chiave API esattamente come facevamo prima
        this.mistralApiKey = mistralApiKey;
    }

    // 1. Definiamo un Record per l'Output Strutturato
    record Filmografia(String attore, List<String> film) {}

    /**
     * ESEMPIO 1: Output Strutturato Nativo
     * Mistral supporta la forzatura del JSON Schema in modo nativo e velocissimo.
     */
    @GetMapping("/filmografia")
    public Filmografia estraiFilm() {
        logger.info("🎬 Avvio Mistral per generazione JSON Strutturato...");

        return chatClient.prompt()
                .user("Genera la filmografia di 5 film famosi di Leonardo DiCaprio.")
                .call()
                // Spring AI capisce automaticamente che Mistral supporta il JSON nativo!
                .entity(Filmografia.class);
    }

    /**
     * ESEMPIO 2: Visione Pura con Pixtral
     * Usiamo il modello specializzato "pixtral-large-latest" per analizzare un'immagine web.
     */
    @GetMapping("/guarda")
    public String guardaImmagine() {
        logger.info("👁️ Avvio Pixtral per l'analisi visiva...");

        return chatClient.prompt()
                .user(u -> {
                            try {
                                u.text("Cosa vedi in questa immagine?")
                                        .media(org.springframework.util.MimeTypeUtils.IMAGE_PNG,
                                                java.net.URI.create("https://docs.spring.io/spring-ai/reference/_images/multimodal.test.png").toURL());
                            } catch (MalformedURLException e) {
                                throw new RuntimeException(e);
                            }
                        }
                )
                .options(MistralAiChatOptions.builder()
                        .model("pixtral-large-latest") // Cambiamo modello al volo!
                        .build())
                .call()
                .content();
    }

    /**
     * ESEMPIO 3: L'Arma Segreta - Estrazione OCR
     * Usiamo l'API a basso livello di Mistral (MistralOcrApi) per "leggere" un PDF
     * senza dover chiedere all'IA di "chattare", risparmiando soldi e allucinazioni!
     */
    @GetMapping("/ocr")
    public String leggiDocumentoOcr() {
        logger.info("📄 Avvio Mistral OCR per estrazione testo da PDF...");

        // Usiamo il client a basso livello fornito da Spring AI
        MistralOcrApi ocrApi = new MistralOcrApi(this.mistralApiKey);

        // Immaginiamo un PDF su internet (o uno aziendale)
        String urlDocumento = "https://arxiv.org/pdf/2201.04234";

        MistralOcrApi.OCRRequest request = new MistralOcrApi.OCRRequest(
                MistralOcrApi.OCRModel.MISTRAL_OCR_LATEST.getValue(),
                "doc-test-1",
                new MistralOcrApi.OCRRequest.DocumentURLChunk(urlDocumento),
                List.of(0), // Vogliamo solo la prima pagina (indice 0)
                true, 5, 50);

        // Facciamo la chiamata diretta all'API OCR
        ResponseEntity<MistralOcrApi.OCRResponse> response = ocrApi.ocr(request);

        return "Testo estratto perfettamente dal PDF:\n\n" + response.getBody().toString();
    }
}