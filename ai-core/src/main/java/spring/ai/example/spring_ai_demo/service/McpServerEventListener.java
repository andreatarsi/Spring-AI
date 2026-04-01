package spring.ai.example.spring_ai_demo.service;

import io.modelcontextprotocol.spec.McpSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.ai.mcp.spec.McpSchema;
//import org.springframework.ai.mcp.spring.annotations.McpLogging;
//import org.springframework.ai.mcp.spring.annotations.McpToolListChanged;
import org.springaicommunity.mcp.annotation.McpLogging;
import org.springaicommunity.mcp.annotation.McpToolListChanged;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class McpServerEventListener {

    private static final Logger log = LoggerFactory.getLogger(McpServerEventListener.class);

    /**
     * Intercetta i messaggi di log che provengono SPONTANEAMENTE dal server MCP esterno.
     * Puoi usare l'attributo clients = "memory-server" per filtrare solo i log di un server specifico.
     */
    @McpLogging(clients = "memory-server")
    public void handleServerLogs(McpSchema.LoggingMessageNotification notification) {
        log.info("📡 [SERVER MCP ESTERNO] Livello {}: {}", notification.level(), notification.data());
    }

    /**
     * Se il server Node.js si aggiorna a runtime e aggiunge nuovi strumenti,
     * questo metodo scatterà automaticamente!
     */
    @McpToolListChanged(clients = "memory-server")
    public void handleToolListChanged(List<McpSchema.Tool> updatedTools) {
        log.info("🔄 [SERVER MCP ESTERNO] Il server ha cambiato la lista dei tools! Ora offre {} strumenti.", updatedTools.size());
    }
}