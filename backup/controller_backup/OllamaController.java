package backup.controller_backup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;

import jakarta.annotation.PostConstruct;

@RestController
@RequestMapping("/ai/ollama")
public class OllamaController {

    private static final Logger logger = LoggerFactory.getLogger(OllamaController.class);

    // Iniezione diretta della classe specifica!
    @Autowired
    private OllamaChatModel ollamaChatModel;

    private ChatClient chatClient;

    // Costruiamo il client DOPO che Spring ha iniettato il modello
    @PostConstruct
    public void init() {
        this.chatClient = ChatClient.create(ollamaChatModel);
    }

    /**
     * SUPERPOTERE 1: GGUF Auto-Pull
     * Scarica e usa un modello di Hugging Face direttamente tramite Ollama!
     */
    @GetMapping("/huggingface-locale")
    public String modelloEsotico() {
        logger.info("🦙 Avvio Ollama con modello GGUF scaricato da Hugging Face...");

        return chatClient.prompt()
                .user("Spiegami cos'è un buco nero in 3 frasi semplici.")
                .options(OllamaChatOptions.builder()
                        // Passiamo un repository Hugging Face. Se Ollama non ce l'ha, lo scarica!
                        .model("hf.co/bartowski/gemma-2-2b-it-GGUF")
                        .temperature(0.5)
                        .build())
                .call()
                .content();
    }

    /**
     * SUPERPOTERE 2: Thinking Mode (Il Ragionamento Locale)
     * Usiamo il famigerato DeepSeek-R1 sul nostro PC e spiamo i suoi pensieri.
     */
    @GetMapping("/ragiona-locale")
    public String ragionaLocale() {
        logger.info("🧠 Avvio Ollama (DeepSeek-R1) con Thinking Mode...");

        ChatResponse response = chatClient.prompt()
                .user("Quante 'r' ci sono nella parola 'strawberry'?")
                .options(OllamaChatOptions.builder()
                        .model("deepseek-r1") // Assicurati di averlo in locale!
                        .enableThinking()     // Sblocca il ragionamento interno
                        .build())
                .call()
                .chatResponse();

        // Estraiamo il ragionamento dai metadata!
        String pensieri = response.getResult().getMetadata().get("thinking");
        String rispostaFinale = response.getResult().getOutput().getText();

        return "🤔 PENSIERI DEL MODELLO:\n" + pensieri + "\n\n✅ RISPOSTA:\n" + rispostaFinale;
    }

    /**
     * SUPERPOTERE 3: Structured Outputs Nativi
     * Imponiamo uno Schema JSON per l'estrazione dati sicura.
     */
    @GetMapping("/estrazione-sicura")
    public String estrazioneJsonGarantita() {
        logger.info("📄 Avvio Ollama con JSON Schema imposto...");

        String schemaJson = """
            {
                "type": "object",
                "properties": {
                    "paesi": {
                        "type": "array",
                        "items": { "type": "string" }
                    }
                },
                "required": ["paesi"]
            }
            """;

        return chatClient.prompt()
                .user("Elenca 3 paesi europei che confinano con l'Italia.")
                .options(OllamaChatOptions.builder()
                        .model("llama3.2")
                        // Ollama DEVE rispettare questo schema alla lettera!
                        .outputSchema(schemaJson)
                        .build())
                .call()
                .content();
    }

    /**
     * EXTRA 1: Il Modello Esterno (Hugging Face GGUF)
     * Usiamo il modello Llama 3.2 da 1B che Spring ha scaricato per noi da Hugging Face.
     */
    /**
     * EXTRA 1: Il Modello Esterno (La Prova del Nove 🕵️‍♂️)
     */
    @GetMapping("/modello-esterno")
    public String usaModelloEsterno() {
        logger.info("🛸 Avvio Ollama con modello esterno Hugging Face...");

        ChatResponse response = chatClient.prompt()
                .user("Spiegami in due righe cos'è la ricorsione in informatica.")
                .options(OllamaChatOptions.builder()
                        .model("hf.co/bartowski/Llama-3.2-1B-Instruct-GGUF")
                        .temperature(0.8)
                        .build())
                .call()
                // Attenzione: non usiamo più .content(), ma prendiamo l'intero oggetto risposta!
                .chatResponse();

        // 🕵️‍♂️ ESTRAIAMO LE PROVE SCHIACCIANTI!
        String modelloUsato = response.getMetadata().getModel();
        String testoGenerato = response.getResult().getOutput().getText();

        return "🔍 PROVA SCHIACCIANTE - Modello che ha risposto: " + modelloUsato +
                "\n\n📜 RISPOSTA DELL'IA:\n" + testoGenerato;
    }

    /**
     * EXTRA 2: Lo Streaming in Tempo Reale
     * Restituiamo un Flux (Flusso Reattivo) mappato come Server-Sent Events.
     * Questo ti permetterà di vedere il testo generarsi parola per parola nel browser!
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamTesto() {
        logger.info("🌊 Avvio Ollama in modalità Streaming...");

        return chatClient.prompt()
                .user("Scrivi un breve racconto di fantascienza di 3 paragrafi su un'IA che scopre di amare il caffè.")
                .options(OllamaChatOptions.builder()
                        .model("llama3.2") // Usiamo il modello base che hai già scaricato
                        .build())
                .stream() // <-- MAGIA! Usiamo stream() invece di call()
                .content();
    }
}