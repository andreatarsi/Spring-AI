package spring.ai.example.spring_ai_demo.ai.superpowers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@Profile("gemini")
public class GeminiSuperpowerProvider implements SuperpowerProvider {
    private static final Logger log = LoggerFactory.getLogger(GeminiSuperpowerProvider.class);

    @Override
    public ChatOptions getOptions(Boolean webSearch, Boolean deepThinking) {
        if (Boolean.TRUE.equals(webSearch)) {
            try {
                log.info("⚡ GEMINI SUPERPOWER: Attivazione Ricerca Web NATIVA (via Reflection)");
                // 1. Troviamo la classe senza importarla!
                Class<?> optionsClass = Class.forName("org.springframework.ai.google.genai.GoogleGenAiChatOptions");
                // 2. Invochiamo il builder()
                Object builder = optionsClass.getMethod("builder").invoke(null);

                // 3. Troviamo e invochiamo withGoogleSearch(true)
                for (Method m : builder.getClass().getMethods()) {
                    if (m.getName().equals("withGoogleSearch")) {
                        m.invoke(builder, true);
                        break;
                    }
                }
                // 4. Invochiamo build() e restituiamo l'opzione agnostica
                return (ChatOptions) builder.getClass().getMethod("build").invoke(builder);
            } catch (Exception e) {
                log.error("Errore nell'attivazione del superpotere Gemini", e);
            }
        }
        return null;
    }
}