package backup.controller_backup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingOptions;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ai.document.Document; // <-- Importante!

import java.util.List;
import java.util.Map;

import static org.springframework.ai.embedding.EmbeddingResultMetadata.EMPTY;

@RestController
@RequestMapping("/ai/embeddings")
public class EmbeddingController {

    private static final Logger logger = LoggerFactory.getLogger(EmbeddingController.class);
    private final EmbeddingModel embeddingModel;

    // REFACTORING: Blindiamo il controller richiedendo esplicitamente Ollama
    public EmbeddingController(@Qualifier("ollamaEmbeddingModel") EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    @GetMapping("/semplice")
    public Map<String, Object> embeddingSemplice(@RequestParam(defaultValue = "L'Intelligenza Artificiale") String testo) {
        float[] vettore = embeddingModel.embed(testo);
        return Map.of("testo", testo, "dimensioni", vettore.length);
    }

    /**
     * LIVELLO 2: L'approccio Batch.
     * Ottimo quando devi indicizzare un intero documento PDF diviso in paragrafi.
     */
    @GetMapping("/batch")
    public Map<String, Object> embeddingMultiplo() {
        logger.info("📚 Calcolo embedding per lista di testi (Batch)...");

        List<String> documenti = List.of(
                "Come fare la pizza alle mele",
                "Manuale di programmazione Java",
                "Ricetta della torta di mele"
        );

        List<float[]> vettori = embeddingModel.embed(documenti);

        return Map.of(
                "documenti_processati", vettori.size(),
                "dimensioni", embeddingModel.dimensions() // Metodo di utilità dell'interfaccia!
        );
    }

    /**
     * LIVELLO 3: L'approccio Enterprise (Request/Response API).
     * Usato quando ti servono anche i Metadata (es. quanti token hai consumato).
     */
    @GetMapping("/completo")
    public Map<String, Object> embeddingCompleto() {
        logger.info("🛠️ Calcolo embedding con dettagli completi (Metadata)...");

        EmbeddingRequest request = new EmbeddingRequest(
                List.of("Testo di prova per estrarre i metadata"),
                (EmbeddingOptions) EMPTY
        );

        EmbeddingResponse response = embeddingModel.call(request);

        return Map.of(
                "modello_usato", response.getMetadata().getModel(),
                "token_usati", response.getMetadata().getUsage().getTotalTokens()
        );
    }

    /**
     * LIVELLO 4: L'approccio RAG (Documenti e Metadati).
     * Il vero standard Enterprise per salvare dati nei Vector Database.
     */
    @GetMapping("/documento")
    public Map<String, Object> embeddingDocumento() {
        logger.info("📄 Calcolo embedding per un oggetto Document con Metadati...");

        // 1. Creiamo un "Documento" con il suo contenuto e i suoi metadati
        Document contratto = new Document(
                "L'abbonamento mensile ha un costo di 9.99€ e può essere disdetto in qualsiasi momento.",
                Map.of(
                        "nome_file", "Termini_e_Condizioni_2026.pdf",
                        "categoria", "Legale",
                        "autore", "Ufficio Contratti",
                        "data_aggiornamento", "2026-03-18"
                )
        );

        // 2. Chiediamo al modello di calcolare il vettore.
        // NOTA: Alcuni modelli furbi includeranno anche i metadati nel calcolo matematico!
        float[] vettore = embeddingModel.embed(contratto);

        // 3. Osserva la magia di Spring AI: ha generato un ID univoco in automatico!
        return Map.of(
                "id_documento_generato", contratto.getId(),
                "metadati_allegati", contratto.getMetadata(),
                "testo", contratto.getText(),
                "dimensioni_vettore", vettore.length
        );
    }

    /**
     * LIVELLO 5: La Prova del Nove Matematica (Senza Vector DB!)
     * Calcoliamo manualmente la distanza semantica tra due frasi.
     */
    @GetMapping("/similarita")
    public Map<String, Object> calcolaSimilarita(
            @RequestParam(defaultValue = "Mi piace mangiare la pizza") String testo1,
            @RequestParam(defaultValue = "Adoro gustare una buona margherita") String testo2) {

        logger.info("📐 Calcolo similarità semantica tra due testi...");

        // 1. Estraiamo i vettori spaziali
        float[] v1 = embeddingModel.embed(testo1);
        float[] v2 = embeddingModel.embed(testo2);

        // 2. Matematica pura: Calcoliamo il Prodotto Scalare (Dot Product)
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (int i = 0; i < v1.length; i++) {
            dotProduct += v1[i] * v2[i];
            norm1 += Math.pow(v1[i], 2);
            norm2 += Math.pow(v2[i], 2);
        }

        // 3. Calcoliamo la Similarità del Coseno (Cosine Similarity)
        double similarita = dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));

        // 4. Trasformiamo in percentuale per renderla più leggibile
        double percentuale = Math.round(similarita * 10000.0) / 100.0;

        return Map.of(
                "testo_1", testo1,
                "testo_2", testo2,
                "score_similarita", percentuale + "%",
                "interpretazione", percentuale > 80 ? "Concetti molto simili!" : "Concetti diversi"
        );
    }
}