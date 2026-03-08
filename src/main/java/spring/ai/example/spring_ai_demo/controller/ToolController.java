package spring.ai.example.spring_ai_demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import spring.ai.example.spring_ai_demo.service.ServizioOrdini;

@RestController
@RequestMapping("/ai/tools")
public class ToolController {

    private static final Logger logger = LoggerFactory.getLogger(ToolController.class);
    private final ChatClient chatClient;

    // 1. Iniettiamo il nostro nuovo servizio
    private final ServizioOrdini servizioOrdini;

    public ToolController(@Qualifier("llamaClient") ChatClient chatClient, ServizioOrdini servizioOrdini) {
        this.chatClient = chatClient;
        this.servizioOrdini = servizioOrdini;
    }

    @GetMapping("/ordine")
    public String chiediStatoOrdine(
            @RequestParam(defaultValue = "Ciao! Puoi dirmi dov'è il mio ordine numero 12345?") String domanda) {

        logger.info("👤 Domanda dell'utente: {}", domanda);

        return chatClient.prompt()
                .system("Sei un assistente per un servizio clienti e-commerce. " +
                        "Hai a disposizione uno strumento (tool) per controllare lo stato degli ordini. " +
                        "REGOLA FONDAMENTALE: NON INVENTARE MAI INFORMAZIONI SUGLI ORDINI. " +
                        "Devi obbligatoriamente usare il tool 'controllaStatoOrdine' passandogli l'ID fornito dall'utente. " +
                        "Usa i dati restituiti dal tool per rispondere all'utente.")
                .user(domanda)
                // 2. MAGIA: Passiamo direttamente l'istanza dell'oggetto (servizioOrdini),
                // non più la stringa! Così Spring non può sbagliarsi.
                .tools(servizioOrdini)
                .call()
                .content();
    }
}