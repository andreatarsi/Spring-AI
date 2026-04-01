package spring.ai.example.spring_ai_demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

// Usiamo i pacchetti della Community per le annotazioni
import org.springaicommunity.mcp.annotation.McpLogging;
import io.modelcontextprotocol.spec.McpSchema.LoggingMessageNotification;

@Component
public class MemoryServerListener {

    private static final Logger log = LoggerFactory.getLogger(MemoryServerListener.class);

    // Il parametro 'clients' DEVE coincidere con il nome nello YAML
    // Se nello YAML hai scritto "memory-server", scrivi "memory-server" qui!
    @McpLogging(clients = "memory-server")
    public void ascoltaLogDelNodeJs(LoggingMessageNotification notification) {

        log.info("📡 [NODE.JS MEMORY SERVER LOG] Livello: {} | Logger: {} | Messaggio: {}",
                notification.level(),
                notification.logger(),
                notification.data());
    }
}