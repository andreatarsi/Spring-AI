package backup.controller_backup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/ai/metadata")
public class MetadataController {

    private static final Logger logger = LoggerFactory.getLogger(MetadataController.class);
    private final ChatClient chatClient;
    private final ChatClient auditedClient;

    public MetadataController(
            @Qualifier("llamaClient") ChatClient chatClient,
            @Qualifier("auditedClient") ChatClient auditedClient) {
        this.chatClient = chatClient;
        this.auditedClient = auditedClient;
    }

    // ==========================================
    // 1. METADATI SUL MESSAGGIO UTENTE
    // ==========================================
    @GetMapping("/user")
    public String userMetadata(@RequestParam(defaultValue = "Come funziona Spring Boot?") String message) {
        // Simuliamo un ID transazione univoco
        String traceId = UUID.randomUUID().toString();

        logger.info(">> Preparazione richiesta AI. TraceID assegnato: {}", traceId);

        // Mappa di metadata multipli (come visto nel corso)
        Map<String, Object> userMetadata = Map.of(
                "traceId", traceId,
                "userId", "andrea-admin",
                "timestamp", System.currentTimeMillis()
        );

        return chatClient.prompt()
                .user(u -> u.text(message)
                        .metadata(userMetadata) // Inseriamo l'intera mappa
                        .metadata("vip-customer", true)) // Possiamo anche concatenare singoli valori
                .call()
                .content();
    }

    // ==========================================
    // 2. METADATI SUL MESSAGGIO DI SISTEMA E DEFAULT
    // ==========================================
    @GetMapping("/system")
    public String systemMetadata(@RequestParam(defaultValue = "Ho un problema con il mio account.") String message) {

        logger.info(">> Inoltro richiesta al dipartimento customer-service...");

        // Usiamo l'auditedClient che ha GIÀ i metadata di default (app-version, department)
        // e ne aggiungiamo di nuovi on-the-fly!
        return auditedClient.prompt()
                .system(s -> s.text("Sei un operatore del supporto tecnico.")
                        .metadata("ticket-category", "tech-support"))
                .user(u -> u.text(message)
                        .metadata("urgency", "HIGH")) // Sovrascrive o si aggiunge al default
                .call()
                .content();
    }

    // ==========================================
    // 3. LOG DEI METADATA TRAMITE ADVISOR (Best Practice)
    // ==========================================
    @GetMapping("/log-extract")
    public String extractAndLogMetadata() {

        // 1. Prepariamo i nostri metadati
        Map<String, Object> myMetadata = Map.of(
                "userId", "user-456",
                "priority", "high",
                "sessionToken", "abc-123"
        );

        logger.info("=== INVIANDO LA RICHIESTA CON SIMPLE LOGGER ADVISOR ===");

        // 2. Usiamo la Fluent API per creare il messaggio e aggiungiamo l'Advisor
        return chatClient.prompt()
                .user(u -> u.text("Che tempo fa oggi?")
                        .metadata(myMetadata)) // Iniettiamo i metadati
                // Il SimpleLoggerAdvisor intercetterà la richiesta e la stamperà in console!
                .advisors(new org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor())
                .call()
                .content();
    }
}