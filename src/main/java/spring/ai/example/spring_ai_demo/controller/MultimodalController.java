package spring.ai.example.spring_ai_demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai/multimodal")
public class MultimodalController {

    private static final Logger logger = LoggerFactory.getLogger(MultimodalController.class);
    private final ChatClient chatClient;

    // INIEZIONE BLINDATA: Chiediamo esplicitamente a Spring il "visionModel"
    // e costruiamo il client qui. Nessuna interferenza con l'AiConfig globale!
    public MultimodalController(@Qualifier("visionModel") ChatModel visionModel) {
        this.chatClient = ChatClient.builder(visionModel).build();
    }

    @GetMapping("/guarda")
    public String guardaImmagine() {

        logger.info("👀 Richiesta di analisi visiva in corso...");

        // 1. Carichiamo l'immagine direttamente dalle risorse del progetto Spring Boot
        ClassPathResource imageResource = new ClassPathResource("static/test.jpeg");

        // 2. Usiamo l'API fluida di ChatClient per inviare Testo + Media
        return chatClient.prompt()
                .user(u -> u.text("Cosa vedi in questa immagine? Descrivila in italiano con massimo 3 frasi.")
                        // Aggiungiamo la foto al messaggio dell'utente specificando che è un PNG
                        .media(MimeTypeUtils.IMAGE_JPEG, imageResource))
                .call()
                .content();
    }
}