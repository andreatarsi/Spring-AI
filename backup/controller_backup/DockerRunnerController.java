package backup.controller_backup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import spring.ai.example.spring_ai_demo.tools.MockWeatherService; // Il nostro fido Tool!

//@RestController
@RequestMapping("/ai/docker")
public class DockerRunnerController {

    private static final Logger logger = LoggerFactory.getLogger(DockerRunnerController.class);
    private final ChatClient chatClient;

    public DockerRunnerController(@Qualifier("openAiChatModel") ChatModel chatModel) {
        this.chatClient = ChatClient.create(chatModel);
    }

    /**
     * ESEMPIO 1: Chiamata base al modello locale in Docker
     */
    @GetMapping("/domanda-base")
    public String domandaBase() {
        logger.info("🐳 Avvio Docker Model Runner (Modello Gemma 3)...");

        return chatClient.prompt()
                .user("Spiegami in una frase cos'è un container Docker.")
                .options(OpenAiChatOptions.builder()
                        .model("ai/gemma3:4B-F16") // Assicurati che corrisponda a quello nel properties!
                        .temperature(0.7)
                        .build())
                .call()
                .content();
    }

    /**
     * ESEMPIO 2: Function Calling in Locale
     * Anche se il modello gira sul nostro PC dentro Docker, riesce a usare i Tool Java!
     */
    @GetMapping("/meteo-locale")
    public String meteoLocale() {
        logger.info("🛠️ Avvio Docker Model Runner con Tool Calling...");

        return chatClient.prompt()
                .user("Che tempo fa ad Amsterdam oggi?")
                // Ancora una volta, riusiamo lo stesso identico codice del MockWeatherService!
                .tools(new MockWeatherService())
                .options(OpenAiChatOptions.builder()
                        .model("ai/gemma3:4B-F16")
                        .build())
                .call()
                .content();
    }
}