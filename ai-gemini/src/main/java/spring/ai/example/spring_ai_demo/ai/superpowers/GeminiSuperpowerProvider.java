package spring.ai.example.spring_ai_demo.ai.superpowers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import spring.ai.example.spring_ai_demo.web.ai.superpowers.SuperpowerProvider;


@Component
@Profile("gemini")
public class GeminiSuperpowerProvider implements SuperpowerProvider {
    private static final Logger log = LoggerFactory.getLogger(GeminiSuperpowerProvider.class);

    @Override
    public ChatOptions getOptions(Boolean webSearch, Boolean deepThinking) {
        var builder = GoogleGenAiChatOptions.builder();
        if (Boolean.TRUE.equals(webSearch)) {
            log.info("⚡ GEMINI SUPERPOWER: (Simulato) Ricerca Web attivata!");
            // TODO: Ripristinare withGoogleSearch(true) quando si usa il client nativo VertexAI
            // o quando esposto nel builder GenAI.
        }
        return builder.build();
    }
}