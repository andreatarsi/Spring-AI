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

// IMPORT SPECIFICI DI OCI (Commentati per non dare errori)
// import org.springframework.ai.oci.genai.OCIEmbeddingOptions;

import java.util.List;
import java.util.Map;

/**
 * Controller per esplorare gli Embeddings di Oracle Cloud (OCI GenAI).
 * Attualmente in letargo per evitare l'errore del file ~/.oci/config mancante!
 */
// @RestController
@RequestMapping("/ai/oci-embeddings")
public class OciGenAiEmbeddingController {

    private static final Logger logger = LoggerFactory.getLogger(OciGenAiEmbeddingController.class);

    private final EmbeddingModel ociEmbeddingModel;

    // Iniettiamo il Bean di Oracle
    public OciGenAiEmbeddingController(@Qualifier("ociEmbeddingModel") EmbeddingModel ociEmbeddingModel) {
        this.ociEmbeddingModel = ociEmbeddingModel;
    }

    /*
    @GetMapping("/base")
    public Map<String, Object> testOciBase(@RequestParam(defaultValue = "Database Autonomo Oracle") String testo) {
        logger.info("Calcolo vettore con OCI GenAI...");

        float[] vettore = ociEmbeddingModel.embed(testo);

        return Map.of(
                "testo", testo,
                "provider", "Oracle Cloud Infrastructure (OCI)",
                "dimensioni", vettore.length
        );
    }

    @GetMapping("/avanzato")
    public Map<String, Object> testOciAvanzato(@RequestParam(defaultValue = "Dati ad altissima sicurezza") String testo) {
        logger.info("Calcolo vettore con OCI GenAI (Override del modello in Runtime)...");

        EmbeddingResponse ociResponse = ociEmbeddingModel.call(
                new EmbeddingRequest(List.of(testo),
                        OCIEmbeddingOptions.builder()
                                .model("un-altro-modello-su-oci")
                                // Magia Enterprise: Se l'azienda ha comprato un cluster IA dedicato su OCI!
                                // .servingMode("dedicated")
                                .build()
                )
        );

        return Map.of(
                "testo_elaborato", testo,
                "provider", "OCI GenAI (Runtime Options)",
                "dimensioni", ociResponse.getResult().getOutput().length
        );
    }
    */
}