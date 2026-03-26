package backup.controller_backup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.ollama.api.OllamaEmbeddingOptions; // L'IMPORT CORRETTO!
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai/ollama-advanced")
public class OllamaAdvancedEmbeddingController {

    private static final Logger logger = LoggerFactory.getLogger(OllamaAdvancedEmbeddingController.class);

    private final EmbeddingModel ollamaEmbeddingModel;

    public OllamaAdvancedEmbeddingController(@Qualifier("ollamaEmbeddingModel") EmbeddingModel ollamaEmbeddingModel) {
        this.ollamaEmbeddingModel = ollamaEmbeddingModel;
    }

    @GetMapping("/tuning")
    public Map<String, Object> testOllamaTuning(@RequestParam(defaultValue = "Ottimizzazione hardware") String testo) {
        logger.info("Calcolo vettore con Ollama (Tuning parametri base)...");

        EmbeddingResponse ollamaResponse = ollamaEmbeddingModel.call(
                new EmbeddingRequest(List.of(testo),
                        OllamaEmbeddingOptions.builder()
                                .model("bge-m3")
                                .build()
                )
        );

        return Map.of(
                "testo", testo,
                "provider", "Ollama (Local Tuning)",
                "dimensioni", ollamaResponse.getResult().getOutput().length
        );
    }

    @GetMapping("/huggingface")
    public Map<String, Object> testOllamaHuggingFace(@RequestParam(defaultValue = "Modello preso da HuggingFace") String testo) {
        logger.info("Calcolo vettore pescando un modello GGUF direttamente da HuggingFace!");

        EmbeddingResponse hfResponse = ollamaEmbeddingModel.call(
                new EmbeddingRequest(List.of(testo),
                        OllamaEmbeddingOptions.builder()
                                .model("hf.co/mixedbread-ai/mxbai-embed-large-v1")
                                .build()
                )
        );

        return Map.of(
                "testo_elaborato", testo,
                "provider", "Ollama + HuggingFace GGUF",
                "dimensioni", hfResponse.getResult().getOutput().length
        );
    }
}