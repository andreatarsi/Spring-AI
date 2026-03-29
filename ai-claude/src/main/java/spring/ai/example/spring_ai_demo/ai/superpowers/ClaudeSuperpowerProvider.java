package spring.ai.example.spring_ai_demo.ai.superpowers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import spring.ai.example.spring_ai_demo.web.ai.superpowers.SuperpowerProvider;


@Component
@Profile("claude")
public class ClaudeSuperpowerProvider implements SuperpowerProvider {
    private static final Logger log = LoggerFactory.getLogger(ClaudeSuperpowerProvider.class);

    @Override
    public ChatOptions getOptions(Boolean webSearch, Boolean deepThinking) {
        var builder = AnthropicChatOptions.builder();

        if (Boolean.TRUE.equals(deepThinking)) {
            log.info("🧠 CLAUDE SUPERPOWER: Deep Thinking Preparato!");
            // Impostiamo la temperatura a 1.0 usando l'API aggiornata del Builder
            builder.temperature(1.0);
        } else {
            builder.temperature(0.0);
        }

        return builder.build();
    }
}