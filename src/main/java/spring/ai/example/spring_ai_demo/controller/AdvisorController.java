package spring.ai.example.spring_ai_demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai/advisors")
public class AdvisorController {

    private static final Logger logger = LoggerFactory.getLogger(AdvisorController.class);
    private final ChatClient chatClient;

    public AdvisorController(@Qualifier("llamaClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    // ==========================================
    // CUSTOM LOGGER ADVISOR
    // ==========================================
    @GetMapping("/custom-log")
    public String customLogger(
            @RequestParam(defaultValue = "Qual è il colmo per un informatico?") String message) {

        // Creiamo il nostro Logger personalizzato.
        // Invece di stampare l'intero mappazzone JSON (che potrebbe contenere dati sensibili),
        // estraiamo e stampiamo SOLO il testo dell'utente e il testo della risposta.
        SimpleLoggerAdvisor customLogger = new SimpleLoggerAdvisor(
                // 1. Funzione per formattare la Richiesta (ChatClientRequest)
                request -> {
                    // Usiamo i metodi esposti dal Prompt interno, come da corso!
                    Object userMessage = request.prompt().getUserMessage();
                    return "\n---> RICHIESTA FILTRATA: " + (userMessage != null ? userMessage.toString() : "Nessun messaggio");
                },
                // 2. Funzione per formattare la Risposta (ChatResponse)
                response -> {
                    // Questa l'avevamo già sistemata ed è corretta
                    String aiText = response.getResult() != null ? response.getResult().getOutput().getText() : "Nessuna risposta";
                    return "\n<--- RISPOSTA FILTRATA: " + aiText;
                },
                0 // 3. Ordine di esecuzione
        );

        logger.info("Eseguo la chiamata con il Custom Logger Advisor...");

        return chatClient.prompt()
                .system("Sei un comico sopraffino")
                .user(message)
                .options(OllamaChatOptions.builder().temperature(0.9).build())
                // Aggiungiamo il nostro Advisor personalizzato alla catena
                .advisors(customLogger)
                .call()
                .content();
    }
}