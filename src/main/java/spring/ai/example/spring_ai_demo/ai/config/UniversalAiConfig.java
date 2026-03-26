package spring.ai.example.spring_ai_demo.ai.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import spring.ai.example.spring_ai_demo.tools.DateTimeTools;
import spring.ai.example.spring_ai_demo.tools.MockWeatherService;

import java.util.Map;

@Configuration
public class UniversalAiConfig {

    private static final Logger log = LoggerFactory.getLogger(UniversalAiConfig.class);

    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder().maxMessages(10).build();
    }

    @Bean
    public ChatClient universalChatClient(ApplicationContext context,
                                          ChatMemory chatMemory,
                                          DateTimeTools dateTimeTools,
                                          MockWeatherService weatherService, // 1. INIETTATO QUI!
                                          AiFeatureProperties props,
                                          Environment env) {

        Map<String, ChatModel> beans = context.getBeansOfType(ChatModel.class);

        if (beans.isEmpty()) {
            throw new IllegalStateException("CRASH: Nessun motore IA trovato! Hai attivato il profilo Maven?");
        }

        String activeProfile = env.getActiveProfiles().length > 0 ? env.getActiveProfiles()[0] : "default";

        ChatModel selectedModel = beans.values().stream()
                .filter(model -> model.getClass().getSimpleName().toLowerCase().contains(activeProfile))
                .findFirst()
                .orElse(beans.values().iterator().next());

        log.info("🚀 Motore AI Selezionato Dinamicamente: {}", selectedModel.getClass().getSimpleName());

        // Invece di chiedere il Builder a Spring, lo creiamo noi dal modello!
        ChatClient.Builder builder = ChatClient.builder(selectedModel);

        // ... (il resto del codice rimane uguale: if systemMsg != null, ecc.) ...

        // 2. CONFIGURAZIONE BUILDER CON FILTRO DI SICUREZZA
        // Verifichiamo che il messaggio non sia nullo prima di passarlo al builder
        String systemMsg = props.getSystemMessage();
        if (systemMsg != null && !systemMsg.isBlank()) {
            builder.defaultSystem(systemMsg);
        } else {
            log.warn("⚠️ System Message non trovato nello YAML. Uso un messaggio di fallback.");
            builder.defaultSystem("Sei un assistente utile.");
        }

        // 3. ADVISORS E TOOLS
        if (props.getFeatures().isLoggingEnabled()) {
            builder.defaultAdvisors(new SimpleLoggerAdvisor());
        }

        if (props.getFeatures().isToolsEnabled()) {
            // 2. AGGIUNTO ALLA LISTA DEGLI STRUMENTI DISPONIBILI!
            builder.defaultTools(dateTimeTools, weatherService);
        }

        return builder.build();
    }
}