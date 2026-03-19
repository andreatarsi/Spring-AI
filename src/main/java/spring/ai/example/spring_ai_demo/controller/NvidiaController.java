package spring.ai.example.spring_ai_demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.ai.example.spring_ai_demo.service.MockWeatherService;

//@RestController
@RequestMapping("/ai/nvidia")
public class NvidiaController {

    private static final Logger logger = LoggerFactory.getLogger(NvidiaController.class);
    private final ChatClient chatClient;

    public NvidiaController(@Qualifier("openAiChatModel") ChatModel chatModel) {
        this.chatClient = ChatClient.create(chatModel);
    }

    /**
     * ESEMPIO: Chiamata standard a NVIDIA NIM con Tool Calling
     * Dimostriamo che l'astrazione di Spring AI resiste anche sui server NVIDIA.
     */
    @GetMapping("/meteo-gpu")
    public String meteoSuNvidia() {
        logger.info("🖥️ Avvio elaborazione su GPU NVIDIA (Llama 3.1) con Tool Calling...");

        return chatClient.prompt()
                .user("C'è bisogno dell'ombrello a Parigi o Amsterdam oggi?")
                .tools(new MockWeatherService()) // Incredibile, funziona anche qui!
                .options(OpenAiChatOptions.builder()
                        .model("meta/llama-3.1-70b-instruct")
                        // NOTA: Se non lo avessimo impostato globalmente nelle properties,
                        // dovremmo OBBLIGATORIAMENTE metterlo qui: .maxTokens(2048)
                        .build())
                .call()
                .content();
    }
}