package backup.controller_backup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

//@RestController
@RequestMapping("/ai/oracle")
public class OciCohereController {

    private static final Logger logger = LoggerFactory.getLogger(OciCohereController.class);
    private final ChatClient chatClient;

    public OciCohereController(@Qualifier("ociGenAiChatModel") ChatModel chatModel) {
        this.chatClient = ChatClient.create(chatModel);
    }

    /**
     * ESEMPIO: Sintesi Enterprise con Cohere su infrastruttura Oracle.
     * I modelli Command-R di Cohere sono addestrati specificamente per
     * il "Retrieval" e l'analisi di documenti aziendali complessi.
     */
    @GetMapping("/sintesi-legale")
    public String sintesiAziendale() {
        logger.info("🏢 Avvio OCI GenAI (Cohere) per analisi documentale sicura...");

        String testoBurocratico = "Ai sensi dell'Articolo 4, comma 2, il dipendente è tenuto a " +
                "presentare la documentazione medica entro e non oltre 48 ore dall'inizio " +
                "dell'assenza. In caso di inadempienza, l'azienda si riserva il diritto di " +
                "sospendere la retribuzione per i giorni non giustificati, previa comunicazione " +
                "scritta tramite raccomandata A/R.";

        return chatClient.prompt()
                .system("Sei un assistente HR in una banca. Devi essere formale e preciso.")
                .user("Sintetizza questo estratto del regolamento in un unico punto elenco chiaro per i dipendenti: " + testoBurocratico)
                .call()
                .content();
    }
}