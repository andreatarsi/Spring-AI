package backup.controller_backup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// IMPORT SPECIFICI DI MINIMAX (Commentati per non dare errori)
// import org.springframework.ai.minimax.MiniMaxEmbeddingOptions;


/**
 * Controller per esplorare gli Embeddings di MiniMax (embo-01).
 * Attualmente in letargo.
 */
// @RestController
@RequestMapping("/ai/minimax-embeddings")
public class MiniMaxEmbeddingController {

    private static final Logger logger = LoggerFactory.getLogger(MiniMaxEmbeddingController.class);

    private final EmbeddingModel minimaxEmbeddingModel;

    // Iniettiamo il Bean generato dall'autoconfigurazione di MiniMax
    public MiniMaxEmbeddingController(@Qualifier("miniMaxEmbeddingModel") EmbeddingModel minimaxEmbeddingModel) {
        this.minimaxEmbeddingModel = minimaxEmbeddingModel;
    }

    /*
    @GetMapping("/base")
    public Map<String, Object> testMinimaxBase(@RequestParam(defaultValue = "Il drago d'oriente") String testo) {
        logger.info("Calcolo vettore con MiniMax AI (Modello embo-01)...");

        float[] vettore = minimaxEmbeddingModel.embed(testo);

        return Map.of(
                "testo", testo,
                "provider", "MiniMax AI",
                "dimensioni", vettore.length
        );
    }

    @GetMapping("/avanzato")
    public Map<String, Object> testMinimaxAvanzato(@RequestParam(defaultValue = "Test override modello") String testo) {
        logger.info("Calcolo vettore con MiniMax AI (Override del modello in Runtime)...");

        // Se MiniMax rilasciasse un nuovo modello, puoi testarlo al volo così,
        // senza toccare l'application.properties!
        EmbeddingResponse minimaxResponse = minimaxEmbeddingModel.call(
                new EmbeddingRequest(List.of(testo),
                        MiniMaxEmbeddingOptions.builder()
                                .model("un-futuro-modello-embo-02")
                                .build()
                )
        );

        return Map.of(
                "testo_elaborato", testo,
                "provider", "MiniMax AI (Custom Model)",
                "dimensioni", minimaxResponse.getResult().getOutput().length
        );
    }
    */
}