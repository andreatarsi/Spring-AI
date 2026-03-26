package backup.controller_backup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ai/prompt-engineering")
public class PromptEngineeringController {

    private final ChatClient chatClient;
    // Aggiungiamo il logger!
    private static final Logger logger = LoggerFactory.getLogger(PromptEngineeringController.class);

    public PromptEngineeringController(@Qualifier("llamaClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/few-shot")
    public String fewShotClassification(
            @RequestParam(defaultValue = "Il nuovo aggiornamento del software mi ha cancellato tutti i file! Sono furioso!") String frase) {

        // 1. Facciamoci sentire in console!
        logger.info("🧠 Analisi Few-Shot in corso per la frase: {}", frase);

        Message systemMessage = new SystemMessage(
                "Sei un analista di sentimenti estremamente preciso. " +
                        "Devi classificare il testo dell'utente restituendo SOLO ed ESCLUSIVAMENTE " +
                        "il sentiment e l'emozione principale in questo formato: 'SENTIMENT: [Positivo/Negativo/Neutrale] | EMOZIONE: [Emozione]'. " +
                        "Non aggiungere nessuna altra parola o spiegazione."
        );

        Message userExample1 = new UserMessage("Ho appena comprato una macchina nuova e va benissimo, sono al settimo cielo!");
        Message assistantExample1 = new AssistantMessage("SENTIMENT: POSITIVO | EMOZIONE: GIOIA");

        Message userExample2 = new UserMessage("Il servizio clienti è stato pessimo, ho aspettato due ore al telefono per niente.");
        Message assistantExample2 = new AssistantMessage("SENTIMENT: NEGATIVO | EMOZIONE: RABBIA");

        Message userExample3 = new UserMessage("Il pacco è arrivato martedì come previsto dalla spedizione standard.");
        Message assistantExample3 = new AssistantMessage("SENTIMENT: NEUTRALE | EMOZIONE: CALMA");

        Message realUserMessage = new UserMessage(frase);

        // 2. MAGIA: Usiamo la nuova interfaccia ChatOptions per impostare la Temperatura a 0.0
        Prompt prompt = new Prompt(
                List.of(
                        systemMessage,
                        userExample1, assistantExample1,
                        userExample2, assistantExample2,
                        userExample3, assistantExample3,
                        realUserMessage
                ),
                ChatOptions.builder()
                        .temperature(0.0) // <-- Niente più "with", si usa direttamente il nome del parametro!
                        .build()
        );

        return chatClient.prompt(prompt)
                .call()
                .content();
    }
}