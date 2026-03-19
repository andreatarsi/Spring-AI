package spring.ai.example.spring_ai_demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel; // <-- IMPORTANTE
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Qualifier; // <-- IMPORTANTE
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

//@RestController
@RequestMapping("/ai/openai-nativo")
public class OpenAiNativeController {

    private static final Logger logger = LoggerFactory.getLogger(OpenAiNativeController.class);
    private final ChatClient chatClient;

    // REFACTORING: Chiediamo esplicitamente il motore di OpenAI usando il Qualifier
    public OpenAiNativeController(@Qualifier("openAiChatModel") ChatModel chatModel) {
        // Creiamo il ChatClient "isolato" e lo leghiamo solo a questo motore
        this.chatClient = ChatClient.create(chatModel);
    }

    @GetMapping("/parametri-segreti")
    public String parametriSegreti() {
        logger.info("🔓 Avvio OpenAI Client Nativo con iniezione di parametri extra...");

        return chatClient.prompt()
                .user("Scrivi una poesia sullo spazio in due righe.")
                .options(OpenAiChatOptions.builder()
                        .model("gpt-4o")
                        .extraBody(Map.of("repetition_penalty", 1.05))
                        .build())
                .call()
                .content();
    }
}