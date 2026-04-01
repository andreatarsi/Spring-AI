package spring.ai.example.spring_ai_demo.web.ai.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class McpContextConverterConfig {

    /*
     * TODO: Scommentare con l'aggiornamento a Spring AI ufficiale.
     * Inietta metadati (es. ID utente) nelle chiamate verso il server MCP.
     *
     * @Bean
     * public org.springframework.ai.mcp.spring.ToolContextToMcpMetaConverter customMcpMetaConverter() {
     * return toolContext -> {
     * if (toolContext == null || toolContext.getContext() == null) return java.util.Map.of();
     * * java.util.Map<String, Object> metadata = new java.util.HashMap<>();
     * metadata.put("system_timestamp", System.currentTimeMillis());
     * return metadata;
     * };
     * }
     */
}