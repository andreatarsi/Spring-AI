package spring.ai.example.spring_ai_demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
//import org.springframework.ai.model.Media;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Controller Sperimentale per Embeddings Multimodali.
 * Trasforma Testo + Immagini + Video nello stesso spazio vettoriale.
 */
// @RestController
@RequestMapping("/ai/vertex-multimodal")
public class VertexAiMultimodalController {

    private static final Logger logger = LoggerFactory.getLogger(VertexAiMultimodalController.class);

    private final EmbeddingModel multimodalModel;

    public VertexAiMultimodalController(@Qualifier("vertexAiMultimodalEmbeddingModel") EmbeddingModel multimodalModel) {
        this.multimodalModel = multimodalModel;
    }

    /*
    @GetMapping("/test-multimodale")
    public Map<String, Object> testMultimodale() {
        logger.info("Generazione embedding combinato (Testo + Immagine)...");

        // 1. Prepariamo un riferimento a un'immagine nei nostri 'resources'
        Media immagine = new Media(MimeTypeUtils.IMAGE_PNG, new ClassPathResource("/static/logo.png"));

        // 2. Creiamo un documento "ibrido"
        Document docMultimodale = new Document("Descrizione di questo logo aziendale", List.of(immagine), Map.of());

        // 3. Calcoliamo l'embedding che rappresenta l'unione di testo e immagine
        // Nota: Qui usiamo l'interfaccia standard, ma il modello sotto è quello Multimodale
        EmbeddingResponse response = multimodalModel.embedForResponse(List.of(docMultimodale.getContent()));

        return Map.of(
                "status", "Vettore generato con successo",
                "dimensioni", response.getResult().getOutput().length,
                "nota", "Questo vettore include informazioni visive e testuali fuse insieme!"
        );
    }
    */
}