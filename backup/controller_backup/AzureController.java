package backup.controller_backup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import spring.ai.example.spring_ai_demo.tools.MockWeatherService; // Il nostro Tool!

//@RestController
@RequestMapping("/ai/azure")
public class AzureController {

    private static final Logger logger = LoggerFactory.getLogger(AzureController.class);
    private final ChatClient chatClient;

    public AzureController(@Qualifier("azureOpenAiChatModel") ChatModel chatModel) {
        this.chatClient = ChatClient.create(chatModel);
    }

    /**
     * ESEMPIO 1: Modelli Classici (GPT-4o) con Multimodalità
     * Usa il parametro standard "maxTokens"
     */
    @GetMapping("/analizza-foto")
    public String analizzaFoto() {
        logger.info("👁️ Avvio Azure GPT-4o per analisi visiva...");

        // Immaginiamo di avere una foto di test
        ClassPathResource imageResource = new ClassPathResource("static/ricetta.jpg");

        return chatClient.prompt()
                .user(u -> u.text("Cosa vedi in questa immagine?")
                        .media(MimeTypeUtils.IMAGE_JPEG, imageResource)
                ) // <- Parentesi chiusa correttamente! ;)
                .options(AzureOpenAiChatOptions.builder()
                        .deploymentName("gpt-4o")
                        .maxTokens(500) // PER I MODELLI CLASSICI USIAMO MAX_TOKENS
                        .build())
                .call()
                .content();
    }

    /**
     * ESEMPIO 2: Modelli "Reasoning" (o1, o3)
     * ATTENZIONE: Questi modelli rifiutano il maxTokens e vogliono maxCompletionTokens!
     */
    @GetMapping("/ragionamento-complesso")
    public String ragionamentoMatematico() {
        logger.info("🧠 Avvio Azure OpenAI o1-preview per calcolo matematico complesso...");

        return chatClient.prompt()
                .user("Risolvi questo problema logico complesso: se 5 gatti catturano 5 topi in 5 minuti, quanto ci mettono 100 gatti a catturare 100 topi?")
                .options(AzureOpenAiChatOptions.builder()
                        .deploymentName("o1-preview") // Immaginiamo di aver schierato il modello o1
                        // LA TRAPPOLA DI AZURE: Per i modelli o1/o3 dobbiamo usare maxCompletionTokens!
                        .maxCompletionTokens(1500)
                        .build())
                .call()
                .content();
    }

    /**
     * ESEMPIO 3: Integrazione Perfetta dei Tool
     * Dimostra come i tool Java funzionino in modo identico anche su Azure
     */
    @GetMapping("/meteo-azure")
    public String chiediMeteoAzure() {
        logger.info("🌤️ Avvio Azure GPT-4o con Tool Calling...");

        return chatClient.prompt()
                .user("Che tempo fa a Londra oggi?")
                // Passiamo lo STESSO IDENTICO tool che avevamo creato per Anthropic!
                // Spring AI fa da "traduttore" universale.
                .tools(new MockWeatherService())
                .options(AzureOpenAiChatOptions.builder()
                        .deploymentName("gpt-4o")
                        .build())
                .call()
                .content();
    }
}