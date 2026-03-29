package spring.ai.example.spring_ai_demo.ai.superpowers;

import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import spring.ai.example.spring_ai_demo.web.ai.superpowers.SuperpowerProvider;

@Component
@Profile("openai")
public class OpenAiSuperpowerProvider implements SuperpowerProvider {
    @Override
    public ChatOptions getOptions(Boolean webSearch, Boolean deepThinking) {
        return OpenAiChatOptions.builder().build();
    }
}