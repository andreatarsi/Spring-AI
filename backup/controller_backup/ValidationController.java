package backup.controller_backup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.StructuredOutputValidationAdvisor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.annotation.JsonProperty;

@RestController
@RequestMapping("/ai/validation")
public class ValidationController {

    private static final Logger logger = LoggerFactory.getLogger(ValidationController.class);
    private final ChatClient chatClient;

    public ValidationController(@Qualifier("llamaClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    // ==========================================
    // 1. IL NOSTRO MODELLO DATI (Record annotato)
    // ==========================================
    // Aiutiamo l'IA e lo Schema Generator dicendo esplicitamente come si chiamano i campi.
    public record Persona(
            @JsonProperty(value = "nome", required = true) String nome,
            @JsonProperty(value = "eta", required = true) int eta, // Senza accento!
            @JsonProperty(value = "professione", required = true) String professione) {}

    // ==========================================
    // 2. ENDPOINT DI ESTRAZIONE CON ADVISOR RICORSIVO
    // ==========================================
    @GetMapping("/estrai")
    public String estraiDati(
            @RequestParam(defaultValue = "Oggi ho incontrato il signor Mario Rossi. Mi ha detto che ha appena compiuto 45 anni e lavora come ingegnere aerospaziale a Milano.") String testo) {

        logger.info("Preparazione dell'estrazione dati dal testo: {}", testo);

        var validationAdvisor = StructuredOutputValidationAdvisor.builder()
                .outputType(Persona.class)
                .maxRepeatAttempts(3)
                .build();

        return chatClient.prompt()
                // Rendiamo il prompt a prova di scemo (o di Llama ribelle)
                .system("Sei un estrattore di dati precisissimo. " +
                        "Analizza il testo e restituisci SOLO un JSON valido. " +
                        "Non usare blocchi markdown. Non aggiungere campi che non ti ho chiesto (es. città). " +
                        "I campi DEVONO CHIAMARSI ESATTAMENTE: 'nome', 'eta' (senza accento) e 'professione'.")
                .user(testo)
                .advisors(validationAdvisor)
                .call()
                .content();
    }
}