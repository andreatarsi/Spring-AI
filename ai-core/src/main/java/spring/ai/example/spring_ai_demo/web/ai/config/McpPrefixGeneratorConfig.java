package spring.ai.example.spring_ai_demo.web.ai.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class McpPrefixGeneratorConfig {

    /*
     * TODO: Scommentare con l'aggiornamento a Spring AI ufficiale.
     * Evita che due server forniscano tool con lo stesso nome causando conflitti.
     *
     * @Bean
     * public org.springframework.ai.mcp.spring.McpToolNamePrefixGenerator customToolNamePrefixGenerator() {
     * return (connectionInfo, tool) -> {
     * String serverName = connectionInfo.clientInfo().name().replaceAll("[^a-zA-Z0-9]", "_");
     * return serverName + "_" + tool.name();
     * };
     * }
     */
}