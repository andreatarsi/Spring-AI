package spring.ai.example.spring_ai_demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/ai/openai-nativo")
public class OpenAiNativeController {

    private static final Logger logger = LoggerFactory.getLogger(OpenAiNativeController.class);
    private final ChatClient chatClient;

    public OpenAiNativeController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * SUPERPOTERE: L'uso di "extraBody"
     * Immagina che la nostra base-url punti a un server vLLM personalizzato
     * che accetta il parametro "repetition_penalty" (che OpenAI non supporta).
     */
    @GetMapping("/parametri-segreti")
    public String parametriSegreti() {
        logger.info("🔓 Avvio OpenAI Client con iniezione di parametri extra nel JSON...");

        ChatResponse response = chatClient.prompt()
                .user("Scrivi una poesia sullo spazio.")
                .options(OpenAiChatOptions.builder()
                        .model("meta-llama/Llama-3-70B-Instruct")
                        // Inietto parametri non standard direttamente nel JSON HTTP!
                        .extraBody(Map.of(
                                "repetition_penalty", 1.05,
                                "min_p", 0.05
                        ))
                        .build())
                .call()
                .chatResponse();

        // Se il modello fosse DeepSeek, potremmo anche spiare i suoi pensieri così:
        String pensieri = response.getResult().getOutput().getMetadata().get("reasoningContent").toString();

        return response.getResult().getOutput().getText();
    }
}