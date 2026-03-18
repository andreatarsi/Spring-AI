package spring.ai.example.spring_ai_demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai/qianfan")
public class QianFanController {

    private static final Logger logger = LoggerFactory.getLogger(QianFanController.class);
    private final ChatClient chatClient;
    private final ImageModel imageModel; // <-- IL NUOVO SUPERPOTERE!

    // Iniettiamo sia il client testuale che quello visivo
    public QianFanController(ChatClient chatClient, ImageModel imageModel) {
        this.chatClient = chatClient;
        this.imageModel = imageModel;
    }

    /**
     * ESEMPIO 1: Consultazione Culturale con ERNIE Bot (Testo)
     */
    @GetMapping("/saggezza-orientale")
    public String saggezzaBaidu() {
        logger.info("🐉 Avvio Baidu QianFan (ERNIE Bot) per consultazione testuale...");
        return chatClient.prompt()
                .user("Spiegami in 3 paragrafi la filosofia dietro la cerimonia del tè cinese (Gongfu Cha).")
                .call()
                .content();
    }

    /**
     * ESEMPIO 2: Generazione Immagini con CogView (Arte)
     * Sfruttiamo il modello visivo di Baidu per creare un'immagine da zero!
     */
    @GetMapping("/disegna-drago")
    public String disegnaConBaidu() {
        logger.info("🎨 Avvio Baidu QianFan (CogView) per generazione immagine...");

        // Chiediamo all'IA di dipingere!
        ImageResponse response = imageModel.call(
                new ImagePrompt("Un drago di giada che vola sopra la Grande Muraglia Cinese, stile pittura tradizionale ad acquerello, alta risoluzione")
        );

        // L'IA ci restituisce l'URL dell'immagine generata che possiamo mostrare nel browser!
        String urlImmagine = response.getResult().getOutput().getUrl();

        return "Ecco la tua opera d'arte generata da CogView: <br><img src='" + urlImmagine + "' />";
    }
}