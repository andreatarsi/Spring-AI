package spring.ai.example.spring_ai_demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

// IMPORT SPECIFICI DI GOOGLE GENAI (Commentati per non dare errori)
// import org.springframework.ai.google.genai.text.GoogleGenAiTextEmbeddingOptions;
// import com.google.genai.types.TaskType;

import java.util.List;
import java.util.Map;

/**
 * Controller per esplorare i nuovi Embeddings di Google GenAI (modello 004+).
 * Attualmente in letargo.
 */
// @RestController
@RequestMapping("/ai/google-embeddings")
public class GoogleGenAiEmbeddingController {

    private static final Logger logger = LoggerFactory.getLogger(GoogleGenAiEmbeddingController.class);

    private final EmbeddingModel googleEmbeddingModel;

    // Iniettiamo il Bean generato dall'autoconfigurazione di Google
    public GoogleGenAiEmbeddingController(@Qualifier("googleGenAiTextEmbeddingModel") EmbeddingModel googleEmbeddingModel) {
        this.googleEmbeddingModel = googleEmbeddingModel;
    }

    /*
    @GetMapping("/base")
    public Map<String, Object> testGoogleBase(@RequestParam(defaultValue = "Il mio primo documento") String testo) {
        logger.info("Calcolo vettore con Google GenAI (TaskType default: RETRIEVAL_DOCUMENT)...");

        float[] vettore = googleEmbeddingModel.embed(testo);

        return Map.of(
                "testo", testo,
                "provider", "Google GenAI",
                "dimensioni", vettore.length,
                "nota", "Se hai impostato dimensions=256 nel properties, la lunghezza sarà 256 invece dei classici 768!"
        );
    }

    @GetMapping("/ricerca")
    public Map<String, Object> testGoogleQuery(@RequestParam(defaultValue = "Come si fa il pane?") String domanda) {
        logger.info("Calcolo vettore Query con Google GenAI...");

        // Override a Runtime: diciamo al modello che questa NON è una frase da salvare,
        // ma è una "Domanda" che deve cercare un documento.
        EmbeddingResponse googleResponse = googleEmbeddingModel.call(
                new EmbeddingRequest(List.of(domanda),
                        GoogleGenAiTextEmbeddingOptions.builder()
                                // Usiamo il task specifico per le query!
                                // .taskType(TaskType.RETRIEVAL_QUERY)
                                .build()
                )
        );

        return Map.of(
                "query", domanda,
                "provider", "Google GenAI (RETRIEVAL_QUERY)",
                "dimensioni", googleResponse.getResult().getOutput().length
        );
    }
    */
}