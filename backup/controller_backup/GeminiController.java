package backup.controller_backup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import spring.ai.example.spring_ai_demo.tools.MockWeatherService;

import java.time.Duration;

//@RestController
@RequestMapping("/ai/gemini")
public class GeminiController {

    private static final Logger logger = LoggerFactory.getLogger(GeminiController.class);
    private final ChatClient chatClient;

    public GeminiController(@Qualifier("googleGenAiChatModel") ChatModel chatModel) {
        this.chatClient = ChatClient.create(chatModel);
    }

    /**
     * SUPERPOTERE 1: Google Search Grounding
     * Collega l'IA a Internet in tempo reale per rispondere a domande sull'attualità.
     */
    @GetMapping("/ricerca-web")
    public String ricercaAttualita() {
        logger.info("🔍 Avvio Gemini con Google Search Grounding...");

        return chatClient.prompt()
                .user("Quali sono state le notizie principali di tecnologia di questa settimana?")
                .options(GoogleGenAiChatOptions.builder()
                        // Magia pura: Gemini cercherà su Google prima di rispondere!
                        .googleSearchRetrieval(true)
                        .build())
                .call()
                .content();
    }

    /**
     * SUPERPOTERE 2: Thinking Budget
     * Diamo a Gemini 2.5 Pro un budget di "pensieri" per risolvere un problema di logica.
     */
    @GetMapping("/budget-pensiero")
    public String pensaProfondamente() {
        logger.info("🧠 Avvio Gemini 2.5 Pro con Thinking Budget allocato...");

        return chatClient.prompt()
                .user("Ho 3 mele, ne mangio 2, ne compro 5, poi divido il totale per 2. Quante mele ho? Spiega il ragionamento.")
                .options(GoogleGenAiChatOptions.builder()
                        .model("gemini-2.5-pro")
                        // Assegniamo fino a 8192 token di "riflessione interna" prima di rispondere
                        .thinkingBudget(8192)
                        // Vogliamo vedere anche i pensieri nella risposta finale (se il modello lo supporta in streaming)
                        .includeThoughts(true)
                        .build())
                .call()
                .content();
    }

    /**
     * SUPERPOTERE 3: Auto-Caching per documenti giganti
     * Mettiamo in cache interi libri in modo automatico per risparmiare il 90% dei costi.
     */
    @GetMapping("/documento-infinito")
    public String analizzaLibro() {
        logger.info("📚 Avvio Gemini con Auto-Caching...");

        // Immagina che questa stringa contenga il testo intero de "I Promessi Sposi"
        String libroGigante = "Quel ramo del lago di Como... [aggiungere altre 100.000 parole]";

        return chatClient.prompt()
                .system(libroGigante)
                .user("Riassumi il capitolo 3.")
                .options(GoogleGenAiChatOptions.builder()
                        // Se il prompt supera i 50.000 token, Spring AI lo mette in cache sui server di Google!
                        .autoCacheThreshold(50000)
                        // La cache durerà un'ora
                        .autoCacheTtl(Duration.ofHours(1))
                        .build())
                .call()
                .content();
    }

    /**
     * SUPERPOTERE 4: Tool Calling + Thought Signatures
     * Quando Gemini 3 Pro "pensa" e usa un Tool, Spring AI gestisce in automatico
     * le firme crittografiche per non perdere il filo del ragionamento!
     */
    @GetMapping("/meteo-ragionato")
    public String meteoConPensiero() {
        logger.info("🌤️ Avvio Gemini 3 Pro con Tool Calling e Thought Signatures...");

        return chatClient.prompt()
                .user("Devo organizzare un picnic a Milano per domani, ma ho paura che piova. Che faccio?")
                .tools(new MockWeatherService()) // Invocherà il nostro servizio Java
                .options(GoogleGenAiChatOptions.builder()
                        .model("gemini-3-pro-preview")
                        // FONDAMENTALE: Obbligatorio quando si usano i Tool con Gemini 3 Pro
                        // per evitare errori HTTP 400!
                        .includeThoughts(true)
                        .build())
                .call()
                .content();
    }

    /**
     * SUPERPOTERE 5: Multimodalità Nativa (Immagini e PDF)
     * Gemini processa nativamente i byte di file complessi.
     */
    @GetMapping("/leggi-tutto")
    public String leggiTutto() {
        logger.info("👁️ Avvio analisi multimodale nativa con Gemini...");

        // Puntiamo al file PDF nelle nostre risorse
        org.springframework.core.io.ClassPathResource pdfResource = new org.springframework.core.io.ClassPathResource("static/contratto-legale.pdf");

        return chatClient.prompt()
                // Usiamo la stessa identica sintassi "blindata" che abbiamo usato per Bedrock!
                .user(u -> u.text("Riassumi i punti salienti di questo documento.")
                        .media(org.springframework.util.MimeType.valueOf("application/pdf"), pdfResource)
                )
                .options(GoogleGenAiChatOptions.builder()
                        .model("gemini-2.0-flash") // Flash è velocissimo per leggere documenti!
                        .build())
                .call()
                .content();
    }
}