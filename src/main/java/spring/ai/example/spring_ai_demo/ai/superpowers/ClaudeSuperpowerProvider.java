package spring.ai.example.spring_ai_demo.ai.superpowers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@Profile("claude")
public class ClaudeSuperpowerProvider implements SuperpowerProvider {
    private static final Logger log = LoggerFactory.getLogger(ClaudeSuperpowerProvider.class);

    @Override
    public ChatOptions getOptions(Boolean webSearch, Boolean deepThinking) {
        try {
            Class<?> optionsClass = Class.forName("org.springframework.ai.anthropic.AnthropicChatOptions");
            Object builder = optionsClass.getMethod("builder").invoke(null);

            Double temperature = Boolean.TRUE.equals(deepThinking) ? 1.0 : 0.0;
            if (temperature == 1.0) log.info("🧠 CLAUDE SUPERPOWER: Deep Thinking Preparato (Temp 1.0)");

            for (Method m : builder.getClass().getMethods()) {
                if (m.getName().equals("withTemperature") && m.getParameterCount() == 1) {
                    m.invoke(builder, temperature);
                    break;
                }
            }
            return (ChatOptions) builder.getClass().getMethod("build").invoke(builder);
        } catch (Exception e) {
            log.error("Errore nell'attivazione del superpotere Claude", e);
        }
        return null;
    }
}