package backup.controller_backup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.io.File;
import java.util.Map;

/**
 * Controller per la Visione Locale tramite Ollama (modello Llava).
 * Analizza le immagini salvate sul PC senza inviare dati nel cloud.
 */
@RestController
@RequestMapping("/ai/ollama-vision")
public class OllamaVisionController {

    private static final Logger logger = LoggerFactory.getLogger(OllamaVisionController.class);

    // Usiamo il ChatModel standard di Ollama, ma configurato con un modello Vision (llava)
    private final ChatModel chatModel;

    public OllamaVisionController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    /**
     * Endpoint per analizzare un'immagine locale.
     * @param filePath Il percorso completo del file immagine (es. "C:/immagini/gatto.jpg")
     * @param prompt La domanda da porre sull'immagine (es. "Cosa vedi?")
     */
    @GetMapping("/analyze")
    public Map<String, String> analyzeImage(
            @RequestParam String filePath,
            @RequestParam(defaultValue = "Descrivi questa immagine in dettaglio.") String prompt) {

        logger.info("Inizio analisi visione locale per il file: {}", filePath);

        // 1. Verifichiamo che il file esista davvero
        File imageFile = new File(filePath);
        if (!imageFile.exists()) {
            return Map.of("errore", "File non trovato al percorso: " + filePath);
        }

        // 2. Creiamo una risorsa Spring dal file locale
        var imageResource = new FileSystemResource(imageFile);

        // 3. Creiamo l'oggetto 'Media' di Spring AI.
        // Dobbiamo specificare il tipo MIME (IMAGE_JPEG o IMAGE_PNG)
        // Per semplicità qui ipotizziamo JPEG, ma andrebbe rilevato dinamicamente.
        Media media = new Media(MimeTypeUtils.IMAGE_JPEG, imageResource);

        // 4. Creiamo il messaggio dell'utente allegando l'immagine.
        // Aggiungiamo Map.of() alla fine per soddisfare il terzo argomento (metadata).
        UserMessage userMessage = UserMessage.builder()
                .text(prompt)
                .media(media)
                .build();

        // 5. Inviamo il prompt al ChatModel di Ollama
        ChatResponse response = chatModel.call(new Prompt(userMessage));

        // 6. Restituiamo il risultato dell'analisi
        String analysis = response.getResult().getOutput().getText();

        return Map.of(
                "file", filePath,
                "domanda", prompt,
                "analisi_locale_ollama", analysis
        );
    }
}