package spring.ai.example.spring_ai_demo.service;

import org.springframework.stereotype.Service;
import org.springaicommunity.mcp.annotation.McpResource;
import org.springaicommunity.mcp.annotation.McpPrompt;
import org.springaicommunity.mcp.annotation.McpComplete;
import org.springaicommunity.mcp.annotation.McpArg;
import io.modelcontextprotocol.spec.McpSchema.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DocumentManagerMcpService {

    // Simuliamo un database in memoria di documenti
    private final Map<String, String> documenti = new ConcurrentHashMap<>();

    public DocumentManagerMcpService() {
        documenti.put("doc-architettura", "L'architettura usa Spring Boot 3 e MCP Protocol.");
        documenti.put("doc-sicurezza", "I server stateless non mantengono la sessione.");
        documenti.put("doc-policy-ferie", "I dipendenti hanno 30 giorni di ferie.");
        documenti.put("report-q1", "Il primo trimestre ha visto un aumento del 20%.");
    }

    // 1. LA RISORSA (Per leggere il documento)
    @McpResource(
            uri = "document://{id}",
            name = "Documento Aziendale",
            description = "Accede a un documento salvato in memoria"
    )
    public ReadResourceResult getDocument(String id) {
        String contenuto = documenti.getOrDefault(id, "Documento non trovato");
        return new ReadResourceResult(List.of(
                new TextResourceContents("document://" + id, "text/plain", contenuto)
        ));
    }

    // 2. IL PROMPT (Per dire all'LLM di riassumerlo)
    @McpPrompt(
            name = "document-summary",
            description = "Genera un prompt per riassumere un documento"
    )
    public GetPromptResult documentSummaryPrompt(
            @McpArg(name = "docId", required = true) String docId) {

        String contenuto = documenti.getOrDefault(docId, "Nessun contenuto.");
        String promptText = "Per favore, riassumi questo documento aziendale in 2 righe:\n\n" + contenuto;

        return new GetPromptResult("Riassunto Documento",
                List.of(new PromptMessage(Role.USER, new TextContent(promptText))));
    }

    // 3. L'AUTO-COMPLETAMENTO (La Vera Magia ✨)
    // Nota come il parametro 'prompt' si lega esattamente al nome del prompt qui sopra!
    @McpComplete(prompt = "document-summary")
    public List<String> completeDocumentId(String prefix) {
        // Se l'utente inizia a scrivere "doc-", noi filtriamo le chiavi e gli diamo i suggerimenti!
        return documenti.keySet().stream()
                .filter(id -> id.toLowerCase().startsWith(prefix.toLowerCase()))
                .sorted()
                .limit(10)
                .toList();
    }
}