package spring.ai.example.spring_ai_demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springaicommunity.mcp.annotation.McpPrompt;
import org.springaicommunity.mcp.annotation.McpResource;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AdvancedMcpServerFeatures {

    private static final Logger log = LoggerFactory.getLogger(AdvancedMcpServerFeatures.class);

    // Dati simulati per il nostro esempio
    private final Map<String, String> internalDocs = Map.of(
            "policy_ferie", "I dipendenti hanno diritto a 30 giorni di ferie annue. Le richieste vanno fatte con 2 settimane di anticipo.",
            "architettura_db", "Il database principale è PostgreSQL 16. La password di root non deve mai essere salvata in chiaro."
    );

    /**
     * 1. RISORSE (@McpResource)
     * Permette ai client MCP di leggere dati statici o file dal nostro server tramite un URI.
     */
    @McpResource(name = "Documentazione Aziendale", uri = "docs://internal/{documentName}")
    public String getInternalDocument(String documentName) {
        log.info("📂 [MCP SERVER] Richiesta lettura risorsa: docs://internal/{}", documentName);

        return internalDocs.getOrDefault(
                documentName.toLowerCase(),
                "Documento non trovato. I documenti disponibili sono: policy_ferie, architettura_db"
        );
    }

    /**
     * 2. PROMPTS (@McpPrompt)
     * Fornisce al Client dei template pre-impostati per interrogare l'IA in modo standardizzato.
     */
    @McpPrompt(name = "code_review", description = "Template standard per fare la Code Review aziendale")
    public String getCodeReviewPrompt() {
        log.info("📝 [MCP SERVER] Richiesta del template di prompt: code_review");

        return """
               Agisci come un Senior Java Developer. 
               Analizza il codice fornito dall'utente rispettando le seguenti regole aziendali:
               1. Segnala variabili non utilizzate.
               2. Verifica la presenza di blocchi try-catch appropriati.
               3. Suggerisci miglioramenti per le performance.
               Fornisci l'output in formato Markdown.
               """;
    }
}