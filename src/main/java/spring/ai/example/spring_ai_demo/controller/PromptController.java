package spring.ai.example.spring_ai_demo.controller;

import org.springframework.ai.chat.client.ChatClient;
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
@RequestMapping("/ai/prompts") // Raggruppiamo questi endpoint
public class PromptController {

    private final ChatClient chatClient;

    // Usiamo il nostro fidato Llama come base per questi test
    public PromptController(@Qualifier("llamaClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * METODO 1: prompt(String content)
     * Quello che abbiamo già usato. Veloce e diretto.
     */
    @GetMapping("/string")
    public String useStringPrompt(@RequestParam(defaultValue = "Dimmi ciao") String message) {
        return chatClient.prompt(message).call().content();
    }

    /**
     * METODO 2: prompt() - Fluent API (Senza argomenti)
     * Perfetto per costruire la richiesta pezzo per pezzo. Molto leggibile!
     */
    @GetMapping("/fluent")
    public String useFluentApi(@RequestParam(defaultValue = "Come si fa la pizza?") String message) {
        return chatClient.prompt() // Iniziamo a costruire...
                .system("Sei un cuoco napoletano molto appassionato. Parla con un po' di dialetto.") // Aggiungiamo il sistema
                .user(message) // Aggiungiamo la domanda dell'utente
                .call() // Eseguiamo
                .content(); // Estraiamo il testo
    }

    /**
     * METODO 3: prompt(Prompt prompt) - API ad Oggetti
     * Ideale per scenari complessi, come passare una cronologia di messaggi passati,
     * o costruire i messaggi programmaticamente (es. iterando su una lista).
     */
    @GetMapping("/object")
    public String usePromptObject(@RequestParam(defaultValue = "Traduci 'Mela'") String message) {

        // 1. Creiamo i singoli messaggi usando le classi specifiche di Spring AI
        Message systemMessage = new SystemMessage("Sei un traduttore infallibile. Rispondi SOLO con la parola tradotta in inglese, senza aggiungere altro testo.");
        Message userMessage = new UserMessage(message);

        // 2. Li mettiamo in una lista di Java (Java 21 List.of)
        List<Message> messageHistory = List.of(systemMessage, userMessage);

        // 3. Creiamo l'oggetto Prompt
        Prompt prompt = new Prompt(messageHistory);

        // 4. Lo passiamo al client
        return chatClient.prompt(prompt).call().content();
    }
}