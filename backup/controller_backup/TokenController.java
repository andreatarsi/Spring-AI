package backup.controller_backup;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/ai/tokens")
public class TokenController {

    private final ChatClient chatClient;

    public TokenController(@Qualifier("llamaClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/conta")
    public Map<String, Object> contaTokens(
            @RequestParam(defaultValue = "Raccontami la storia di Amleto in 3 righe") String messaggio) {

        // Invece di usare .content() che ci dà solo la stringa finale,
        // chiamiamo .chatResponse() per ottenere l'intero pacchetto di dati grezzi!
        ChatResponse response = chatClient.prompt()
                .user(messaggio)
                .call()
                .chatResponse();

        // Estraiamo i metadati e le statistiche di utilizzo (Usage)
        var usage = response.getMetadata().getUsage();

        // Creiamo una mappa ordinata per vedere bene i risultati nel browser in formato JSON
        Map<String, Object> risultati = new LinkedHashMap<>();
        risultati.put("1_domanda", messaggio);

        // CORREZIONE 1: Usiamo getText() invece di getContent()
        if (response.getResult() != null && response.getResult().getOutput() != null) {
            risultati.put("2_risposta_ia", response.getResult().getOutput().getText());
        }

        // Leggiamo i famosi Token!
        if (usage != null) {
            risultati.put("3_token_consumati_per_la_domanda_input", usage.getPromptTokens());

            // CORREZIONE 2: Usiamo getCompletionTokens()
            risultati.put("4_token_generati_per_la_risposta_output", usage.getCompletionTokens());

            risultati.put("5_token_totali_spesi", usage.getTotalTokens());
        } else {
            risultati.put("errore", "Il modello non ha fornito i metadati sui token.");
        }

        return risultati;
    }
}