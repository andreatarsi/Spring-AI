package backup.controller_backup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.deepseek.DeepSeekAssistantMessage; // Classe esclusiva DeepSeek!
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

//@RestController
@RequestMapping("/ai/deepseek")
public class DeepSeekController {

    private static final Logger logger = LoggerFactory.getLogger(DeepSeekController.class);
    private final ChatClient chatClient;

    public DeepSeekController(@Qualifier("deepSeekChatModel") ChatModel chatModel) {
        this.chatClient = ChatClient.create(chatModel);
    }

    /**
     * SUPERPOTERE 1: Chat Prefix Completion
     * Forziamo l'IA a rispondere SOLO con codice Python, mettendole le parole in bocca.
     */
    @GetMapping("/forza-codice")
    public String forzaPython() {
        logger.info("🐍 Avvio DeepSeek con Prefix Completion (Forzatura Python)...");

        // 1. Il nostro comando
        UserMessage userMessage = new UserMessage("Scrivi un algoritmo quick sort.");

        // 2. L'inizio forzato della risposta dell'IA
        Message prefixMessage = DeepSeekAssistantMessage.prefixAssistantMessage("```python\n");

        // 3. Mettiamo tutto insieme e diciamo a DeepSeek di fermarsi non appena chiude il blocco di codice ("```")
        Prompt prompt = new Prompt(
                List.of(userMessage, prefixMessage),
                ChatOptions.builder().stopSequences(List.of("```")).build()
        );

        return chatClient.prompt(prompt).call().content();
    }

    /**
     * SUPERPOTERE 2: Modello di Ragionamento Esposto (deepseek-reasoner)
     * Vediamo i pensieri dell'IA estratti separatamente dalla risposta finale.
     */
    @GetMapping("/ragiona")
    public String ragiona() {
        logger.info("🧠 Avvio deepseek-reasoner per estrarre la Chain of Thought...");

        ChatResponse response = chatClient.prompt()
                .user("Qual è più grande tra 9.11 e 9.8? Spiega il ragionamento.")
                .options(DeepSeekChatOptions.builder()
                        .model("deepseek-reasoner") // Usiamo il modello che "pensa"
                        .build())
                .call()
                .chatResponse();

        // Facciamo il cast al messaggio specifico di DeepSeek per sbloccare i metodi segreti!
        DeepSeekAssistantMessage message = (DeepSeekAssistantMessage) response.getResult().getOutput();

        // Estraiamo la "Chain of Thought" (I pensieri)
        String ragionamento = message.getReasoningContent();
        // Estraiamo la risposta pulita
        String rispostaFinale = message.getText();

        return "🤔 COME HO RAGIONATO:\n" + ragionamento +
                "\n\n✅ LA MIA RISPOSTA:\n" + rispostaFinale;
    }

    /**
     * SUPERPOTERE 3: Chat Multi-Turn con il Reasoner (La Trappola dell'Errore 400)
     * Dimostra come "potare" la memoria per non far crashare l'API di DeepSeek.
     */
    @GetMapping("/ragiona-multiplo")
    public String ragionaMultiplo() {
        logger.info("🧠 Avvio conversazione multi-round con deepseek-reasoner...");

        // ROUND 1: La prima domanda
        List<org.springframework.ai.chat.messages.Message> cronologia = new java.util.ArrayList<>();
        cronologia.add(new UserMessage("Tra 9.11 e 9.8, quale numero è più grande?"));

        DeepSeekChatOptions opzioni = DeepSeekChatOptions.builder()
                .model("deepseek-reasoner")
                .build();

        ChatResponse response1 = chatClient.prompt()
                .messages(cronologia)
                .options(opzioni)
                .call()
                .chatResponse();

        DeepSeekAssistantMessage msg1 = (DeepSeekAssistantMessage) response1.getResult().getOutput();
        String testoRisposta1 = msg1.getText(); // "9.8 è più grande..."

        // IL TRUCCO FONDAMENTALE (Evita l'Errore 400!):
        // Ricostruiamo il messaggio dell'assistente inserendo SOLO il testo pulito,
        // scartando la "Chain of Thought" (i pensieri) del turno precedente!
        cronologia.add(new org.springframework.ai.chat.messages.AssistantMessage(testoRisposta1));

        // ROUND 2: La seconda domanda (che si ricorda della prima)
        cronologia.add(new UserMessage("E quante 'R' ci sono nella parola 'strawberry'?"));

        ChatResponse response2 = chatClient.prompt()
                .messages(cronologia)
                .options(opzioni)
                .call()
                .chatResponse();

        DeepSeekAssistantMessage msg2 = (DeepSeekAssistantMessage) response2.getResult().getOutput();

        return "ROUND 1: " + testoRisposta1 + "\n\n" +
                "ROUND 2 (Pensiero): " + msg2.getReasoningContent() + "\n" +
                "ROUND 2 (Risposta): " + msg2.getText();
    }
}