package spring.ai.example.spring_ai_demo.web.ai.config;

import io.modelcontextprotocol.server.McpServerFeatures;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.ai.mcp.server.McpServerFeatures;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// IMPORTANTI: Usiamo i pacchetti della specifica ufficiale MCP (io.modelcontextprotocol)
import io.modelcontextprotocol.spec.McpSchema;
import java.util.function.BiConsumer;
import java.util.List;

@Configuration
public class McpLowLevelServerConfig {

    private static final Logger log = LoggerFactory.getLogger(McpLowLevelServerConfig.class);

    /**
     * 1. COMPLETIONS (Auto-completamento) a Basso Livello.
     * Diciamo al Client: "Se l'utente sta digitando qualcosa per il prompt 'code-completion',
     * suggeriscigli questi linguaggi di programmazione".
     */
    @Bean
    public List<McpServerFeatures.SyncCompletionSpecification> customCompletions() {

        var completionSpec = new McpServerFeatures.SyncCompletionSpecification(
                // Il riferimento al prompt per cui offriamo il completamento
                new McpSchema.PromptReference("ref/prompt", "code-completion", "Fornisce suggerimenti di codice"),

                // L'Handler: cosa facciamo quando il client ci chiede un suggerimento?
                (exchange, request) -> {
                    log.info("⌨️ [MCP SERVER] Il client ha richiesto l'auto-completamento per: {}", request);

                    // Inviamo un "Ping" di controllo usando l'oggetto Exchange!
                    exchange.ping();

                    // Usiamo la classe annidata CompleteCompletion che hai scovato nel codice sorgente!
                    return new McpSchema.CompleteResult(
                            new McpSchema.CompleteResult.CompleteCompletion(
                                    List.of("java", "python", "typescript", "rust"), // I suggerimenti
                                    10,   // Totale risultati disponibili
                                    true  // Ha altre pagine? (hasMore)
                            )
                    );
                }
        );

        return List.of(completionSpec);
    }

    /**
     * 2. RESOURCE a Basso Livello con PROGRESS NOTIFICATION.
     * Registriamo una risorsa "pesante" bypassando le annotazioni.
     */
    @Bean
    public List<McpServerFeatures.SyncResourceSpecification> heavyDatabaseResource() {

        // Definiamo cosa offriamo
        var systemInfoResource = new McpSchema.Resource(
                "postgres://main-db/schema",
                "Schema Database Principale",
                "Lo schema completo del DB",
                "application/json",
                null
        );

        var resourceSpec = new McpServerFeatures.SyncResourceSpecification(
                systemInfoResource,

                // L'Handler della richiesta
                (exchange, request) -> {
                    log.info("🗄️ [MCP SERVER] Inizio estrazione risorsa pesante...");

                    // I parametri esatti per la v0.17.1: token, progress, total, message
                    exchange.progressNotification(
                            new McpSchema.ProgressNotification(
                                    "db-export-token",
                                    0.5,
                                    1.0,
                                    "Estrazione tabelle in corso..."
                            )
                    );

                    // Mettiamo il server a dormire per 3 secondi per simulare un DB lento
                    try { Thread.sleep(3000); } catch (InterruptedException e) {}

// Inviamo anche un log strutturato al client (Costruttore diretto)
// I parametri sono: livello, nome del logger, messaggio
                    exchange.loggingNotification(
                            new McpSchema.LoggingMessageNotification(
                                    McpSchema.LoggingLevel.INFO,
                                    "DbExtractor",
                                    "Estrazione completata con successo"
                            )
                    );

                    String dummyContent = "{ \"tables\": [\"users\", \"orders\", \"products\"] }";

                    // Restituiamo il file richiesto
                    return new McpSchema.ReadResourceResult(
                            List.of(new McpSchema.TextResourceContents(
                                    request.uri(), "application/json", dummyContent
                            ))
                    );
                }
        );

        return List.of(resourceSpec);
    }

    /**
     * ROOT LIST CHANGES
     * Si attiva quando il Client (es. l'Inspector o Claude Desktop) cambia
     * le cartelle a cui ci dà accesso.
     */
    @Bean
    public BiConsumer<io.modelcontextprotocol.server.McpSyncServerExchange, List<McpSchema.Root>> rootsChangeHandler() {
        return (exchange, roots) -> {
            log.info("📂 [MCP SERVER] Il client ha aggiornato le sue Roots (cartelle accessibili): {}", roots);
            // Qui potresti aggiornare la logica del tuo server in base ai nuovi permessi
        };
    }
}