package spring.ai.example.spring_ai_demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
//import org.springframework.ai.openai.OpenAiSdkEmbeddingOptions; // Nota il package SDK
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Controller per l'integrazione UFFICIALE OpenAI SDK.
 * Supporta OpenAI, Azure Foundry e GitHub Models.
 */
// @RestController
@RequestMapping("/ai/openai-sdk")
public class OpenAiSdkEmbeddingController {

    private static final Logger logger = LoggerFactory.getLogger(OpenAiSdkEmbeddingController.class);

    private final EmbeddingModel sdkEmbeddingModel;

    public OpenAiSdkEmbeddingController(@Qualifier("openAiEmbeddingModel") EmbeddingModel sdkEmbeddingModel) {
        this.sdkEmbeddingModel = sdkEmbeddingModel;
    }

    /*
    @GetMapping("/embed")
    public Map<String, Object> testSdk(@RequestParam(defaultValue = "Test con SDK Ufficiale") String testo) {
        logger.info("Calcolo vettore con OpenAI SDK Ufficiale...");

        // Esempio di chiamata con dimensioni ridotte (feature dei modelli v3)
        EmbeddingResponse response = sdkEmbeddingModel.call(
                new EmbeddingRequest(List.of(testo),
                        OpenAiSdkEmbeddingOptions.builder()
                                .model("text-embedding-3-small")
                                .dimensions(256) // Risparmiamo spazio!
                                .build()
                )
        );

        return Map.of(
                "testo", testo,
                "provider", "OpenAI Official SDK",
                "dimensioni", response.getResult().getOutput().length
        );
    }
    */
}