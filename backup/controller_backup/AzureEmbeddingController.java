package backup.controller_backup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController; // IN LETARGO!

// IMPORT SPECIFICI DI AZURE (Commentati per non far arrabbiare IntelliJ finché non metti la libreria nel pom.xml)
// import org.springframework.ai.azure.openai.AzureOpenAiEmbeddingOptions;


/**
 * Controller per esplorare gli Embeddings tramite Microsoft Azure OpenAI.
 */
// @RestController
@RequestMapping("/ai/azure-embeddings")
public class AzureEmbeddingController {

    private static final Logger logger = LoggerFactory.getLogger(AzureEmbeddingController.class);

    // NOTA: il nome del bean autoconfigurato da Spring per Azure è azureOpenAiEmbeddingModel
    private final EmbeddingModel azureEmbeddingModel;

    public AzureEmbeddingController(@Qualifier("azureOpenAiEmbeddingModel") EmbeddingModel azureEmbeddingModel) {
        this.azureEmbeddingModel = azureEmbeddingModel;
    }

    /*
    @GetMapping("/base")
    public Map<String, Object> testAzureBase(@RequestParam(defaultValue = "Sicurezza Enterprise") String testo) {
        logger.info("Calcolo vettore con Azure OpenAI (Default Deployment)...");

        // Usa il 'deployment-name' impostato nel file application.properties
        float[] vettore = azureEmbeddingModel.embed(testo);

        return Map.of(
                "testo", testo,
                "provider", "Azure OpenAI",
                "dimensioni", vettore.length
        );
    }

    @GetMapping("/avanzato")
    public Map<String, Object> testAzureAvanzato(@RequestParam(defaultValue = "Dati riservati") String testo) {
        logger.info("Calcolo vettore con Azure OpenAI (Override Deployment in Runtime)...");

        // Su Azure puoi avere più modelli deployati contemporaneamente (es. uno veloce, uno preciso).
        // Qui ignoriamo l'application.properties e forziamo un deployment specifico per questa chiamata.
        EmbeddingResponse azureResponse = azureEmbeddingModel.call(
                new EmbeddingRequest(List.of(testo),
                        AzureOpenAiEmbeddingOptions.builder()
                                .deploymentName("nome-del-deploy-ad-alta-precisione")
                                .user("id-utente-123") // Utile per il tracciamento dei costi e rate-limiting per singolo utente
                                .build()
                )
        );

        return Map.of(
                "testo", testo,
                "provider", "Azure OpenAI (Runtime Options)",
                "dimensioni", azureResponse.getResult().getOutput().length
        );
    }
    */
}