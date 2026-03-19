package spring.ai.example.spring_ai_demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ai.minimax.MiniMaxChatOptions;
import org.springframework.ai.minimax.api.MiniMaxApi;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//@RestController
@RequestMapping("/ai/minimax")
public class MiniMaxController {

    private static final Logger logger = LoggerFactory.getLogger(MiniMaxController.class);
    private final ChatClient chatClient;

    public MiniMaxController(@Qualifier("miniMaxChatModel") ChatModel chatModel) {
        this.chatClient = ChatClient.create(chatModel);
    }

    /**
     * SUPERPOTERE MINIMAX: Web Search Nativo
     * L'IA naviga su internet in tempo reale per rispondere a domande sull'attualità
     * senza che noi dobbiamo programmare un client HTTP o fare scraping!
     */
    @GetMapping("/ricerca-web")
    public String ricercaWebAsiatica() {
        logger.info("🌏 Avvio MiniMax con Web Search nativo integrato...");

        // Estraiamo il Tool speciale "pre-confezionato" da MiniMax
        List<MiniMaxApi.FunctionTool> strumentoRicerca = List.of(MiniMaxApi.FunctionTool.webSearchFunctionTool());

        return chatClient.prompt()
                .user("Chi ha vinto la medaglia d'oro nei 100 metri alle Olimpiadi di Parigi 2024?")
                .options(MiniMaxChatOptions.builder()
                        .model(MiniMaxApi.ChatModel.ABAB_6_5_G_Chat.getValue())
                        // Iniettiamo lo strumento di ricerca direttamente nelle options!
                        .tools(strumentoRicerca)
                        .build())
                .call()
                .content();
    }
}