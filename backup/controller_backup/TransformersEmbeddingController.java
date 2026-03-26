package backup.controller_backup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;


/**
 * Controller per Transformers locali via ONNX Runtime.
 * Il modello gira direttamente dentro la JVM!
 */
// @RestController
@RequestMapping("/ai/transformers")
public class TransformersEmbeddingController {

    private static final Logger logger = LoggerFactory.getLogger(TransformersEmbeddingController.class);

    private final EmbeddingModel transformersModel;

    public TransformersEmbeddingController(@Qualifier("transformersEmbeddingModel") EmbeddingModel transformersModel) {
        this.transformersModel = transformersModel;
    }

    /*
    @GetMapping("/embed")
    public Map<String, Object> testTransformers(@RequestParam(defaultValue = "Embedding locale ultra-veloce") String testo) {
        logger.info("Calcolo vettore con Transformer ONNX locale (nella JVM)...");

        long startTime = System.currentTimeMillis();
        float[] vettore = transformersModel.embed(testo);
        long endTime = System.currentTimeMillis();

        return Map.of(
                "testo", testo,
                "provider", "Transformers (ONNX Runtime)",
                "dimensioni", vettore.length,
                "tempo_ms", (endTime - startTime),
                "info", "Eseguito localmente senza chiamate HTTP esterne!"
        );
    }
    */
}