package spring.ai.example.spring_ai_demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
// import org.springframework.ai.vertexai.embedding.VertexAiTextEmbeddingOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Controller per Google Vertex AI (Enterprise GCP).
 * Richiede autenticazione tramite gcloud o Service Account JSON.
 */
// @RestController
@RequestMapping("/ai/vertex-embeddings")
public class VertexAiEmbeddingController {

    private static final Logger logger = LoggerFactory.getLogger(VertexAiEmbeddingController.class);

    private final EmbeddingModel vertexModel;

    public VertexAiEmbeddingController(@Qualifier("vertexAiEmbeddingModel") EmbeddingModel vertexModel) {
        this.vertexModel = vertexModel;
    }

    /*
    @GetMapping("/embed")
    public Map<String, Object> testVertex(@RequestParam(defaultValue = "Dati protetti nel cloud Google") String testo) {
        logger.info("Calcolo vettore con Google Vertex AI...");

        EmbeddingResponse response = vertexModel.call(
                new EmbeddingRequest(List.of(testo),
                        VertexAiTextEmbeddingOptions.builder()
                                .model("text-embedding-004")
                                .taskType("RETRIEVAL_QUERY")
                                .build()
                )
        );

        return Map.of(
                "testo", testo,
                "provider", "Google Vertex AI",
                "dimensioni", response.getResult().getOutput().length
        );
    }
    */
}