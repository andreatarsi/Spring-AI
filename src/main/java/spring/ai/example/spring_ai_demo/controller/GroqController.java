package spring.ai.example.spring_ai_demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import spring.ai.example.spring_ai_demo.service.MockWeatherService;

@RestController
@RequestMapping("/ai/groq")
public class GroqController {

    private static final Logger logger = LoggerFactory.getLogger(GroqController.class);
    private final ChatClient chatClient;

    public GroqController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * ESEMPIO 1: Lo Streaming a velocità folle
     * Chiediamo un testo lunghissimo e restituiamo un flusso reattivo (Flux).
     */
    @GetMapping("/velocita")
    public Flux<String> testVelocita() {
        logger.info("🏎️ Avvio Groq Llama3 per test di velocità estrema in Streaming...");

        return chatClient.prompt()
                .user("Scrivi un saggio di 500 parole sull'impatto dell'intelligenza artificiale nello sviluppo software. Sii dettagliato.")
                .options(OpenAiChatOptions.builder()
                        .model("llama3-70b-8192")
                        .build())
                // Usiamo .stream() invece di .call() per non aspettare la fine!
                .stream()
                .content();
    }

    /**
     * ESEMPIO 2: Tool Calling Ultra-Rapido
     * Verifica l'infrastruttura mostrata nel diagramma "Custom Function"
     */
    @GetMapping("/meteo-rapido")
    public String toolCallingGroq() {
        logger.info("⚡ Avvio Groq per Tool Calling rapido...");

        return chatClient.prompt()
                .user("Che tempo fa a Roma e Milano? Sii conciso.")
                // Anche la Ferrari di Groq capisce i nostri vecchi Tool Java!
                .tools(new MockWeatherService())
                .options(OpenAiChatOptions.builder()
                        .model("llama3-70b-8192")
                        .build())
                .call()
                .content();
    }
}