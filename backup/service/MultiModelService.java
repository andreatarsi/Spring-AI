package backup.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class MultiModelService {

    private static final Logger logger = LoggerFactory.getLogger(MultiModelService.class);

    // Partiamo da una "base" solida (il nostro llamaClient)
    private final ChatClient baseClient;

    public MultiModelService(@Qualifier("llamaClient") ChatClient baseClient) {
        this.baseClient = baseClient;
    }

    public String testMutateFlow() {
        try {
            // 1. Usa .mutate() per creare una variante "Creativa"
            // Eredita tutto (URL, modello llama3.2), ma cambia il System Prompt
            ChatClient creativeClient = baseClient.mutate()
                    .defaultSystem("Sei uno scrittore di romanzi fantasy. Usa metafore e un linguaggio poetico.")
                    .build();

            // 2. Usa .mutate() per creare una variante "Analitica"
            // Stessa base, ma comportamento opposto
            ChatClient analyticalClient = baseClient.mutate()
                    .defaultSystem("Sei un analista dati robotico. Rispondi a elenchi puntati, sii freddo e conciso.")
                    .build();

            String prompt = "Descrivi l'oceano.";

            // Facciamo la chiamata con entrambi i client mutati
            String creativeResponse = creativeClient.prompt(prompt).call().content();
            String analyticalResponse = analyticalClient.prompt(prompt).call().content();

            // Loggiamo i risultati
            logger.info("=== Risposta CREATIVA ===\n{}", creativeResponse);
            logger.info("=== Risposta ANALITICA ===\n{}", analyticalResponse);

            return "Test completato! Controlla la console del tuo IDE.";

        } catch (Exception e) {
            logger.error("Errore nel flusso multi-client", e);
            return "Errore!";
        }
    }
}