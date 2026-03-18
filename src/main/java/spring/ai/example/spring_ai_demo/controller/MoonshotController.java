package spring.ai.example.spring_ai_demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai/moonshot")
public class MoonshotController {

    private static final Logger logger = LoggerFactory.getLogger(MoonshotController.class);
    private final ChatClient chatClient;

    public MoonshotController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * SUPERPOTERE MOONSHOT: Contesto da 128.000 Token!
     * Ideale per fargli analizzare log di sistema giganteschi, interi libri
     * o decine di file di codice concatenati.
     */
    @PostMapping("/analizza-mega-documento")
    public String analizzaDocumentoGigante(@RequestBody String documentoInfinito) {
        logger.info("🌕 Avvio Moonshot AI per analisi di un testo massivo...");

        return chatClient.prompt()
                // Gli diamo in pasto il documento gigante (fino a ~100.000 parole)
                .system("Sei un analista esperto. Analizza il seguente documento: " + documentoInfinito)
                // Gli facciamo una domanda super specifica
                .user("Trovami il nome del protagonista e riassumi il capitolo 12 in 3 righe.")
                .call()
                .content();
    }
}