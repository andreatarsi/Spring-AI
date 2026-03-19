package spring.ai.example.spring_ai_demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.openai.OpenAiEmbeddingOptions; // Nota il package differente dall'SDK
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Controller per OpenAI (Implementazione Standard Spring AI).
 */
// @RestController
@RequestMapping("/ai/openai-standard")
public class OpenAiStandardEmbeddingController {

    private static final Logger logger = LoggerFactory.getLogger(OpenAiStandardEmbeddingController.class);

    private final EmbeddingModel openAiModel;

    // Usiamo il Qualifier specifico per la versione standard
    public OpenAiStandardEmbeddingController(@Qualifier("openAiEmbeddingModel") EmbeddingModel openAiModel) {
        this.openAiModel = openAiModel;
    }

    /*
    @GetMapping("/embed")
    public Map<String, Object> testStandard(@RequestParam(defaultValue = "La semplicità è l'ultima sofisticazione") String testo) {
        logger.info("Calcolo vettore con OpenAI Standard...");

        EmbeddingResponse response = openAiModel.call(
                new EmbeddingRequest(List.of(testo),
                        OpenAiEmbeddingOptions.builder()
                                .model("text-embedding-3-large")
                                .dimensions(1024)
                                .build()
                )
        );

        return Map.of(
                "testo", testo,
                "provider", "OpenAI Standard",
                "dimensioni", response.getResult().getOutput().length
        );
    }
    */
}