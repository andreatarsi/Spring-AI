package spring.ai.example.spring_ai_demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai/zhipu")
public class ZhiPuController {

    private static final Logger logger = LoggerFactory.getLogger(ZhiPuController.class);
    private final ChatClient chatClient;
    private final ImageModel imageModel; // <-- Aggiungiamo il pennello!

    public ZhiPuController(ChatClient chatClient, ImageModel imageModel) {
        this.chatClient = chatClient;
        this.imageModel = imageModel;
    }

    /**
     * ESEMPIO 1: Enigma Logico (Thinking Mode)
     */
    @GetMapping("/enigma-logico")
    public String risolviEnigma() {
        logger.info("🧠 Avvio ZhiPu AI (GLM-4) con Thinking Mode attivato...");
        return chatClient.prompt()
                .user("Ci sono tre scatole. Una contiene solo mele... [ecc ecc]")
                .options(ZhiPuAiChatOptions.builder()
                        .model("glm-4-plus")
                        .build())
                .call()
                .content();
    }

    /**
     * ESEMPIO 2: Generazione Immagine con CogView-3
     * Testiamo la capacità di rendering di ZhiPu AI.
     */
    @GetMapping("/cyber-panda")
    public String dipingiPanda() {
        logger.info("🐼 Avvio ZhiPu AI (CogView-3) per generazione immagine...");

        ImageResponse response = imageModel.call(
                new ImagePrompt("Un adorabile panda rosso vestito a tema cyberpunk che programma al computer in una piovosa strada di Shanghai illuminata al neon. Stile 3D render, altissima risoluzione, 4k.")
        );

        String urlImmagine = response.getResult().getOutput().getUrl();

        return "<h3>La creazione di CogView-3:</h3><br><img src='" + urlImmagine + "' style='max-width: 600px;'/>";
    }
}