package spring.ai.example.spring_ai_demo.web.ai.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpClientCustomizerConfig {

    private static final Logger log = LoggerFactory.getLogger(McpClientCustomizerConfig.class);

    /*
     * TODO: Scommentare con l'aggiornamento a Spring AI ufficiale.
     * Permette di settare timeout hard-coded e funzionalità avanzate per ogni singola connessione.
     *
     * @Bean
     * public org.springframework.ai.mcp.client.McpSyncClientCustomizer customMcpSyncClientCustomizer() {
     * return (serverConfigurationName, spec) -> {
     * log.info("⚙️ Configurazione Timeout per server: {}", serverConfigurationName);
     * spec.requestTimeout(java.time.Duration.ofSeconds(15));
     * };
     * }
     */
}