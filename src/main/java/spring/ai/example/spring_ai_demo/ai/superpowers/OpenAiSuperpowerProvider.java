package spring.ai.example.spring_ai_demo.ai.superpowers;

import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("openai")
public class OpenAiSuperpowerProvider implements SuperpowerProvider {
    @Override
    public ChatOptions getOptions(Boolean webSearch, Boolean deepThinking) {
        // Nessun superpotere "nativo" da attivare per ora,
        // per OpenAI si usano i "Tools" agnostici!
        return null;
    }
}