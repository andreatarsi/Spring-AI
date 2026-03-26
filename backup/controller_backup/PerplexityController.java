package backup.controller_backup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;

//@RestController
@RequestMapping("/ai/perplexity")
public class PerplexityController {

    private static final Logger logger = LoggerFactory.getLogger(PerplexityController.class);
    private final ChatClient chatClient;

    // Usiamo il motore di OpenAI come "cavallo di Troia" per Perplexity
    public PerplexityController(@Qualifier("openAiChatModel") ChatModel chatModel) {
        this.chatClient = ChatClient.create(chatModel);
    }

    /**
     * ESEMPIO: Ricerca Web in Tempo Reale
     * I modelli "online" di Perplexity navigano sul web prima di rispondere.
     */
    @GetMapping("/notizie-fresche")
    public String ricercaNotizie() {
        logger.info("🔎 Avvio Perplexity AI (Sonar Online) per ricerca web...");

        return chatClient.prompt()
                // Chiediamo una cosa che cambia in continuazione!
                .user("Qual è la quotazione attuale delle azioni Apple (AAPL) e quali sono le ultime due notizie a riguardo di oggi?")
                .options(OpenAiChatOptions.builder()
                        // Assicuriamoci di usare un modello con il suffisso "-online"
                        .model("llama-3.1-sonar-small-128k-online")
                        .build())
                .call()
                .content();
    }
}