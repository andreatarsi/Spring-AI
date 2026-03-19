package spring.ai.example.spring_ai_demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
// import org.springframework.ai.postgresml.PostgresMlEmbeddingOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Controller per PostgresML.
 * Calcola gli embeddings direttamente dentro il database PostgreSQL!
 */
// @RestController
@RequestMapping("/ai/postgresml")
public class PostgresMlEmbeddingController {

    private static final Logger logger = LoggerFactory.getLogger(PostgresMlEmbeddingController.class);

    private final EmbeddingModel pgmlModel;

    public PostgresMlEmbeddingController(@Qualifier("postgresMlEmbeddingModel") EmbeddingModel pgmlModel) {
        this.pgmlModel = pgmlModel;
    }

    /*
    @GetMapping("/embed")
    public Map<String, Object> testPgml(@RequestParam(defaultValue = "L'intelligenza è vicina ai dati") String testo) {
        logger.info("Calcolo vettore tramite PostgresML (dentro il DB)...");

        // Esempio di override runtime per usare un modello diverso caricato in Postgres
        EmbeddingResponse response = pgmlModel.call(
                new EmbeddingRequest(List.of(testo),
                        PostgresMlEmbeddingOptions.builder()
                                .transformer("intfloat/e5-small")
                                .build()
                )
        );

        return Map.of(
                "testo", testo,
                "provider", "PostgresML",
                "dimensioni", response.getResult().getOutput().length
        );
    }
    */
}