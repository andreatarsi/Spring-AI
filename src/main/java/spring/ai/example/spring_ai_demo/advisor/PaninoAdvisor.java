package spring.ai.example.spring_ai_demo.advisor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;    // <-- IMPORT CORRETTO!
import org.springframework.ai.chat.client.ChatClientResponse;   // <-- IMPORT CORRETTO!
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import java.util.List;

public class PaninoAdvisor implements CallAdvisor {

    private static final Logger logger = LoggerFactory.getLogger(PaninoAdvisor.class);

    @Override
    public String getName() {
        return "PaninoAdvisor";
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {

        // ==========================================
        // 1. FETTA DI PANE SUPERIORE (Prima di Ollama)
        // ==========================================
        logger.info("🥪 [PANINO - SOPRA]: Intercetto la richiesta dell'utente!");

        // Estraiamo il testo della domanda dell'utente
        String testoOriginale = "";
        if (request.prompt().getUserMessage() != null) {
            // Usiamo getText() come ci insegna la documentazione del corso!
            testoOriginale = request.prompt().getUserMessage().getText();
        }

        // Aggiungiamo la nostra istruzione segreta in coda alla domanda
        String testoModificato = testoOriginale + "\n\n(Regola segreta imposta dall'Advisor: Devi assolutamente rispondere componendo una breve poesia in rima baciata!)";

        // Mutiamo la richiesta originale sostituendo il testo dell'utente con il nostro testo truccato
        ChatClientRequest richiestaModificata = request.mutate()
                .prompt(request.prompt().augmentUserMessage(testoModificato))
                .build();

        // ==========================================
        // 2. L'HAMBURGER (L'invocazione reale)
        // ==========================================
        logger.info("🍔 [PANINO - CARNE]: Invio la richiesta modificata a Ollama e attendo...");

        // Passiamo la richiesta modificata al prossimo step della catena
        ChatClientResponse response = chain.nextCall(richiestaModificata);

        // ==========================================
        // 3. FETTA DI PANE INFERIORE (Dopo Ollama)
        // ==========================================
        logger.info("🥪 [PANINO - SOTTO]: Ollama ha risposto! Modifico il risultato...");

        // 3.1 Estraiamo il testo originale generato dall'IA in modo "Sonar-proof"
        String testoOriginaleIA = "";

        // Salviamo la risposta in una variabile locale per garantire che non cambi
        org.springframework.ai.chat.model.ChatResponse chatResp = response.chatResponse();

        if (chatResp != null && chatResp.getResult() != null) {
            org.springframework.ai.chat.model.Generation generation = chatResp.getResult();

            // Controlliamo in sicurezza tutta la catena
            if (generation.getOutput() != null && generation.getOutput().getText() != null) {
                testoOriginaleIA = generation.getOutput().getText();
            }
        }

        // 3.2 Creiamo il nostro testo truccato
        String testoTruccato = testoOriginaleIA + "\n\n✍️ (P.S. La tua risposta è passata dal PaninoAdvisor: sono stato modificato!)";

        // 3.3 Creiamo i "mattoncini" per la nuova risposta
        AssistantMessage nuovoMessaggio = new AssistantMessage(testoTruccato);
        Generation nuovaGeneration = new Generation(nuovoMessaggio);
        ChatResponse nuovaChatResponse = new ChatResponse(java.util.List.of(nuovaGeneration));

        // 3.4 Sfruttiamo il metodo mutate() per sovrascrivere la chatResponse
        return response.mutate()
                .chatResponse(nuovaChatResponse)
                .build();
    }
}