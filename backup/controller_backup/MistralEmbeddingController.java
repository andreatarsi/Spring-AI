package backup.controller_backup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// IMPORT SPECIFICI DI MISTRAL AI (Commentati per non dare errori)
// import org.springframework.ai.mistralai.MistralAiEmbeddingOptions;


/**
 * Controller per esplorare gli Embeddings di Mistral AI.
 * Attualmente in letargo.
 */
// @RestController
@RequestMapping("/ai/mistral-embeddings")
public class MistralEmbeddingController {

    private static final Logger logger = LoggerFactory.getLogger(MistralEmbeddingController.class);

    private final EmbeddingModel mistralEmbeddingModel;

    // Iniettiamo il Bean generato dall'autoconfigurazione di Mistral
    public MistralEmbeddingController(@Qualifier("mistralAiEmbeddingModel") EmbeddingModel mistralEmbeddingModel) {
        this.mistralEmbeddingModel = mistralEmbeddingModel;
    }

    /*
    @GetMapping("/testo")
    public Map<String, Object> testMistralTesto(@RequestParam(defaultValue = "La Tour Eiffel è bellissima") String testo) {
        logger.info("Calcolo vettore con Mistral AI (Modello Testo Generale)...");

        // Usa il 'mistral-embed' impostato di default
        float[] vettore = mistralEmbeddingModel.embed(testo);

        return Map.of(
                "testo", testo,
                "provider", "Mistral AI (Testo)",
                "dimensioni", vettore.length // Dovrebbe essere 1024
        );
    }

    @GetMapping("/codice")
    public Map<String, Object> testMistralCodice(@RequestParam(defaultValue = "public static void main(String[] args) {}") String codice) {
        logger.info("Calcolo vettore con Mistral AI (Modello Specifico per CODICE)...");

        // Magia di Mistral: cambiamo il modello al volo per usare Codestral!
        EmbeddingResponse mistralResponse = mistralEmbeddingModel.call(
                new EmbeddingRequest(List.of(codice),
                        MistralAiEmbeddingOptions.builder()
                                .withModel("codestral-embed")
                                .build()
                )
        );

        return Map.of(
                "codice", codice,
                "provider", "Mistral AI (Codestral)",
                "dimensioni", mistralResponse.getResult().getOutput().length, // Dovrebbe essere 1536
                "nota", "Questo vettore è ottimizzato per la sintassi e la semantica dei linguaggi di programmazione!"
        );
    }
    */
}