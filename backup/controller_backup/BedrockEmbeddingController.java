package backup.controller_backup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.ai.bedrock.cohere.api.CohereEmbeddingRequest.InputType;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController; // SEMPRE IN LETARGO!


/**
 * Controller per esplorare gli Embeddings tramite AWS Bedrock.
 */
// @RestController
@RequestMapping("/ai/bedrock-embeddings")
public class BedrockEmbeddingController {

    private static final Logger logger = LoggerFactory.getLogger(BedrockEmbeddingController.class);
    private final EmbeddingModel cohereEmbeddingModel;

    public BedrockEmbeddingController(@Qualifier("bedrockCohereEmbeddingModel") EmbeddingModel cohereEmbeddingModel) {
        this.cohereEmbeddingModel = cohereEmbeddingModel;
    }

  /*  @GetMapping("/cohere-avanzato")
    public Map<String, Object> testCohereAvanzato(
            @RequestParam(defaultValue = "Il manuale della caldaia modello X200") String documento,
            @RequestParam(defaultValue = "Come si resetta la caldaia X200?") String domanda) {

        logger.info("Calcolo vettori ottimizzati con AWS Bedrock Cohere...");

        // 1. Incorporiamo il Documento da salvare nel DB (Ottimizzato per lo storage)
        EmbeddingResponse docResponse = cohereEmbeddingModel.call(
                new EmbeddingRequest(List.of(documento),
                        BedrockCohereEmbeddingOptions.builder()
                                .inputType(InputType.SEARCH_DOCUMENT)
                                .build())
        );

        // 2. Incorporiamo la Domanda dell'utente (Ottimizzata per la ricerca)
        EmbeddingResponse queryResponse = cohereEmbeddingModel.call(
                new EmbeddingRequest(List.of(domanda),
                        BedrockCohereEmbeddingOptions.builder()
                                .inputType(InputType.SEARCH_QUERY)
                                .build())
        );

        return Map.of(
                "doc_elaborato", documento,
                "doc_dimensioni", docResponse.getResult().getOutput().length,
                "query_elaborata", domanda,
                "query_dimensioni", queryResponse.getResult().getOutput().length,
                "nota", "I due vettori sono stati generati con intenti semantici diversi!"
        );
    }

   */

    /*
    @GetMapping("/titan-avanzato")
    public Map<String, Object> testTitanAvanzato(@RequestParam(defaultValue = "La foresta amazzonica") String testo) {

        logger.info("Calcolo vettore con AWS Bedrock Titan...");

        // NOTA ARCHITETTURALE: Titan NON supporta il batching.
        // Dobbiamo assicurarci di passargli sempre e solo una singola stringa nella List.
        // Se facessimo List.of(testo1, testo2), Amazon Bedrock restituirebbe un errore!

        EmbeddingResponse titanResponse = titanEmbeddingModel.call(
                new EmbeddingRequest(List.of(testo),
                        // BedrockTitanEmbeddingOptions.builder()
                        //      .inputType(InputType.TEXT) // Specifichiamo che è un testo, non un'immagine Base64
                        //      .build()
                        org.springframework.ai.embedding.EmbeddingOptionsBuilder.builder().build()
                )
        );

        return Map.of(
                "testo_elaborato", testo,
                "provider", "AWS Bedrock - Titan",
                "dimensioni", titanResponse.getResult().getOutput().length,
                "avviso", "Ricorda: Titan supporta anche vettori da immagini Base64!"
        );
    }
    */
}