package backup.controller_backup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.bedrock.converse.api.BedrockCacheOptions;
import org.springframework.ai.bedrock.converse.api.BedrockCacheStrategy;
import org.springframework.ai.bedrock.converse.BedrockChatOptions;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import spring.ai.example.spring_ai_demo.tools.MockWeatherService;

//@RestController
@RequestMapping("/ai/bedrock")
public class BedrockController {

    private static final Logger logger = LoggerFactory.getLogger(BedrockController.class);
    private final ChatClient chatClient;

    // Qui inietteremo il client di Bedrock.
    // NOTA: Se provi a lanciare questo endpoint ORA (con Ollama), andrà in errore
    // perché stiamo usando BedrockChatOptions. Serve solo come "template" per il futuro!
    public BedrockController(@Qualifier("bedrockCohereChatModel") ChatModel chatModel) {
        this.chatClient = ChatClient.create(chatModel);
    }

    /**
     * ESEMPIO 1: Analisi di un intero documento PDF (Novità Bedrock/Claude)
     */
    @GetMapping("/analizza-contratto")
    public String analizzaContratto() {

        logger.info("📄 Preparazione analisi PDF con AWS Bedrock...");

        ClassPathResource pdfResource = new ClassPathResource("static/contratto-legale.pdf");

        return chatClient.prompt()
                // APRIAMO IL MESSAGGIO UTENTE (Lambda)
                .user(u -> u.text("Sei un avvocato. Analizza questo documento e riassumi i rischi principali in 3 punti.")
                        .media(org.springframework.util.MimeType.valueOf("application/pdf"), pdfResource)
                ) // <--- ECCO LA PARENTESI MAGICA CHE CHIUDE LA LAMBDA!
                // ORA POSSIAMO CHIAMARE L'IA
                .call()
                .content();
    }

    /**
     * ESEMPIO 2: Il Prompt Caching (Risparmio sui costi Cloud)
     */
    @GetMapping("/domanda-economica")
    public String domandaConCache() {

        logger.info("💰 Avvio richiesta con Prompt Caching attivato...");

        String grandeManualeAziendale = "Qui ci sarebbero migliaia di righe di testo sulle policy aziendali...";

        return chatClient.prompt()
                .system(grandeManualeAziendale)
                .user("Quanti giorni di ferie mi spettano al primo anno?")
                // Usiamo le opzioni specifiche di Bedrock
                .options(BedrockChatOptions.builder()
                        // Diciamo ad AWS di mettere in Cache il System Prompt (il manuale)
                        // Così alla prossima domanda non pagheremo i token di lettura!
                        .cacheOptions(BedrockCacheOptions.builder()
                                .strategy(BedrockCacheStrategy.SYSTEM_ONLY)
                                .build())
                        .build())
                .call()
                .content();
    }

    /**
     * ESEMPIO 3: Analisi Video (Esclusiva dei modelli Amazon Nova)
     */
    @GetMapping("/analizza-video")
    public String analizzaVideo() {
        logger.info("🎥 Preparazione analisi Video MP4 con Amazon Nova...");

        // Immaginiamo di avere un breve video MP4 nelle risorse
        ClassPathResource videoResource = new ClassPathResource("static/test.video.mp4");

        return chatClient.prompt()
                .user(u -> u.text("Guarda questo video e descrivimi l'azione principale che avviene.")
                        // Usiamo il mime type per i video MP4
                        .media(org.springframework.util.MimeType.valueOf("video/mp4"), videoResource)
                )
                .call()
                .content();
    }

    /**
     * ESEMPIO 4: Tool Calling (Uso di strumenti esterni)
     */
    @GetMapping("/meteo")
    public String chiediMeteo() {
        logger.info("🌤️ Avvio richiesta con Tool Calling attivato...");

        return chatClient.prompt()
                .user("Ciao! Dovrei fare un viaggio a Parigi domani. Mi sai dire com'è il meteo lì?")
                // Passiamo l'istanza della nostra classe all'IA
                // L'IA capirà che non sa il meteo di Parigi, metterà in pausa la risposta,
                // eseguirà il nostro codice Java, leggerà "22°C", e poi formulerà la risposta finale!
                .tools(new MockWeatherService())
                .call()
                .content();
    }

    /**
     * ESEMPIO 5: Batch Code Review con Caching (Use Case dal mondo reale)
     */
    @GetMapping("/code-review")
    public String revisioneCodice() {
        logger.info("👨‍💻 Avvio Batch Code Review con Prompt Caching...");

        // Un mega-prompt di sistema con tutte le regole aziendali (almeno 1024 token per essere cachato bene)
        String regoleAziendali = "Sei un Senior Java Developer. Le nostre regole di sicurezza prevedono che...";

        String codiceDaAnalizzare = "public class Login { public boolean check(String p) { return p.equals(\"admin\"); } }";

        return chatClient.prompt()
                .system(regoleAziendali)
                .user("Fai la code review di questo frammento: \n" + codiceDaAnalizzare)
                .options(BedrockChatOptions.builder()
                        // Usiamo SYSTEM_ONLY per mettere in cache le regole e non ripagarle per ogni file!
                        .cacheOptions(BedrockCacheOptions.builder()
                                .strategy(BedrockCacheStrategy.SYSTEM_ONLY)
                                .build())
                        // AWS raccomanda i Claude 3.5 Sonnet o 3.7 per queste task complesse
                        .model("us.anthropic.claude-3-5-sonnet-20240620-v1:0")
                        .build())
                .call()
                .content();
    }

    /**
     * ESEMPIO 6: Observability e Tracciamento dei Costi
     */
    @GetMapping("/calcola-risparmio")
    public String calcolaRisparmio() {
        logger.info("📊 Avvio richiesta con estrazione delle metriche di cache...");

        // Effettuiamo la chiamata chiedendo a Spring di restituirci l'intero oggetto ChatResponse
        org.springframework.ai.chat.model.ChatResponse response = chatClient.prompt()
                .system("Manuale aziendale lunghissimo da mettere in cache...")
                .user("Qual è la policy sui rimborsi?")
                .options(BedrockChatOptions.builder()
                        .model("us.anthropic.claude-3-5-sonnet-20240620-v1:0")
                        .cacheOptions(BedrockCacheOptions.builder()
                                .strategy(BedrockCacheStrategy.SYSTEM_ONLY) //
                                .build())
                        .build())
                .call()
                .chatResponse();

        // FIX: Sostituito getGenerationTokens con getCompletionTokens!
        org.springframework.ai.chat.metadata.Usage usage = response.getMetadata().getUsage();
        if (usage != null) {
            logger.info("📊 REPORT TOKEN -> Inviati: {}, Generati: {}, Totali: {}",
                    usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens());
        } else {
            logger.warn("⚠️ Nessun dato di utilizzo token restituito dal modello.");
        }

        // FIX 2: Usiamo getText() invece di getContent() !
        return "Risposta dell'IA: " + response.getResult().getOutput().getText() +
                "\n(Controlla i log per vedere i token usati!)";
    }
}