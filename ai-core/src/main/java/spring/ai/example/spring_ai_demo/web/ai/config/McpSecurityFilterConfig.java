package spring.ai.example.spring_ai_demo.web.ai.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpSecurityFilterConfig {

    private static final Logger log = LoggerFactory.getLogger(McpSecurityFilterConfig.class);

    /* * TODO: Scommentare quando si aggiorna a Spring AI ufficiale (M5 o superiore).
     * Attualmente McpToolFilter non è presente nella libreria community 0.8.0.
     * * @Bean
     * public org.springframework.ai.mcp.spring.McpToolFilter customMcpToolFilter() {
     * return (connectionInfo, tool) -> {
     * if (tool.name().toLowerCase().contains("delete")) {
     * log.warn("🛡️ [SICUREZZA] Bloccato l'accesso al tool: {}", tool.name());
     * return false;
     * }
     * return true;
     * };
     * }
     */
}