package spring.ai.example.spring_ai_demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.anthropic.api.AnthropicApi;
import org.springframework.ai.anthropic.AnthropicSkillsResponseHelper;
import org.springframework.ai.anthropic.api.CitationDocument;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.ai.example.spring_ai_demo.service.MockWeatherService;

import java.util.List;

@RestController
@RequestMapping("/ai/anthropic")
public class AnthropicController {

    private static final Logger logger = LoggerFactory.getLogger(AnthropicController.class);
    private final ChatClient chatClient;

    public AnthropicController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * SUPERPOTERE 1: Modalità "Thinking" (Il flusso di coscienza)
     */
    @GetMapping("/pensa")
    public String chiediConPensiero() {
        logger.info("🧠 Avvio Claude 3.7 in modalità Thinking...");

        ChatResponse response = chatClient.prompt()
                .user("Quanti numeri primi esistono tali che n mod 4 == 3? Spiega il ragionamento passo passo.")
                .options(AnthropicChatOptions.builder()
                        // Abilitiamo il pensiero visibile con un budget di 2048 token!
                        .thinking(AnthropicApi.ThinkingType.ENABLED, 2048)
                        .build())
                .call()
                .chatResponse();

        // Estraiamo sia la risposta finale che il "pensiero segreto" di Claude!
        AssistantMessage message = response.getResult().getOutput();
        String pensiero = (String) message.getMetadata().get("thinking");
        String rispostaFinale = message.getText();

        return "🤔 PENSIERO INTERNO DI CLAUDE:\n" + pensiero +
                "\n\n✅ RISPOSTA FINALE:\n" + rispostaFinale;
    }

    /**
     * SUPERPOTERE 2: Citazioni Anti-Allucinazione
     */
    @GetMapping("/citazioni")
    public String analizzaConCitazioni() {
        logger.info("🎯 Avvio Analisi con Citazioni di Precisione...");

        // Costruiamo un documento di testo abilitando le citazioni
        CitationDocument document = CitationDocument.builder()
                .plainText("Il progetto segreto 'Apollo' sarà lanciato il 15 Novembre 2025. Il budget allocato è di 4 milioni di Euro.")
                .title("Documento Segreto Aziendale")
                .citationsEnabled(true) // Fondamentale!
                .build();

        ChatResponse response = chatClient.prompt()
                .user("Qual è il budget del progetto Apollo e quando verrà lanciato?")
                .options(AnthropicChatOptions.builder()
                        .citationDocuments(document) // Passiamo il documento al modello
                        .build())
                .call()
                .chatResponse();

        // Nel mondo reale qui scansioneremmo la lista di citazioni per validare la risposta!
        Integer citationCount = (Integer) response.getMetadata().get("citationCount");

        return "Risposta: " + response.getResult().getOutput().getText() +
                "\n(Il modello ha usato " + citationCount + " citazioni esatte dal testo per rispondere!)";
    }

    /**
     * SUPERPOTERE 3: Skills (Generazione di file Excel reali!)
     */
    @GetMapping("/genera-excel")
    public String generaFileExcel() {
        logger.info("📊 Richiesta generazione di un foglio di calcolo Excel (.xlsx)...");

        ChatResponse response = chatClient.prompt()
                .user("Crea un foglio Excel con i dati di vendita del Q1 2025. " +
                        "Includi le colonne Mese, Entrate, Uscite e Profitto con 3 righe di dati inventati.")
                .options(AnthropicChatOptions.builder()
                        // Ordiniamo a Claude di usare l'abilità per creare file XLSX!
                        .skill(AnthropicApi.AnthropicSkill.XLSX)
                        .build())
                .call()
                .chatResponse();

        // Estraiamo gli ID dei file generati dai server di Anthropic
        List<String> fileIds = AnthropicSkillsResponseHelper.extractFileIds(response);

        if (fileIds.isEmpty()) {
            return "Claude non ha generato nessun file. Ha risposto a parole: " + response.getResult().getOutput().getText();
        }

        // NOTA: Per scaricare fisicamente il file, nel mondo reale ignetteremo 'AnthropicApi' nel costruttore
        // e useremmo anthropicApi.downloadFile(fileId). Per ora ci accontentiamo dell'ID!
        return "🎉 SUCCESSO! Claude ha generato il file Excel. File ID sui server Anthropic: " + fileIds.get(0);
    }

    /**
     * SUPERPOTERE 4: Tool Choice ANY (Obbligo di usare almeno uno strumento)
     * Ottimo per l'efficienza: evita che l'IA "chiacchieri" a vuoto quando sai che le serve un dato esterno.
     */
    @GetMapping("/tool-forzato")
    public String toolForzato() {
        logger.info("🛠️ Avvio richiesta con obbligo di utilizzo Tool (ANY)...");

        return chatClient.prompt()
                // Anche se la domanda sembra innocua, l'IA è OBBLIGATA a usare il tool!
                .user("Ciao, come stai oggi? E che tempo fa a Roma?")
                .tools(new MockWeatherService()) // Il nostro finto servizio meteo
                .options(AnthropicChatOptions.builder()
                        // Ordiniamo: "Scegli tu quale, ma DEVI usare un tool!"
                        .toolChoice(new AnthropicApi.ToolChoiceAny())
                        .build())
                .call()
                .content();
    }

    /**
     * SUPERPOTERE 5: Tool Choice TOOL (Obbligo di usare un tool SPECIFICO)
     * Ottimo per la validazione: garantisce che un'operazione critica venga eseguita.
     */
    @GetMapping("/tool-specifico")
    public String toolSpecifico() {
        logger.info("🎯 Avvio richiesta con obbligo di utilizzo di un Tool SPECIFICO...");

        return chatClient.prompt()
                .user("Parlami un po' di Milano.")
                .tools(new MockWeatherService())
                .options(AnthropicChatOptions.builder()
                        // Ordiniamo: "Devi usare ESATTAMENTE il tool che si chiama 'weatherByLocation'"
                        // (Spring AI usa il nome del metodo Java come nome del tool di default)
                        .toolChoice(new AnthropicApi.ToolChoiceTool("weatherByLocation"))
                        .build())
                .call()
                .content();
    }

    /**
     * SUPERPOTERE 6: Tool Choice NONE (Disattivazione temporanea)
     * Ottimo per il controllo: disabilita temporaneamente l'accesso ai tool (es. se l'utente non ha i permessi)
     * senza doverli rimuovere dalla configurazione globale.
     */
    @GetMapping("/tool-disabilitato")
    public String toolDisabilitato() {
        logger.info("🚫 Avvio richiesta con Tool disabilitati (NONE)...");

        return chatClient.prompt()
                .user("Che tempo fa a Napoli?")
                .tools(new MockWeatherService()) // Passiamo il tool...
                .options(AnthropicChatOptions.builder()
                        // ...ma gli vietiamo categoricamente di usarlo!
                        .toolChoice(new AnthropicApi.ToolChoiceNone())
                        .build())
                .call()
                .content();
        // Risponderà: "Mi dispiace, non ho accesso a dati meteo in tempo reale..."
    }

    /**
     * SUPERPOTERE 7: Disattivazione Tool Paralleli (Esecuzione Sequenziale)
     * Se chiedi 5 città, l'IA di default lancia 5 chiamate in parallelo.
     * Se il tuo database non regge il carico, la forzi a farne una per volta!
     */
    @GetMapping("/tool-sequenziali")
    public String toolSequenziali() {
        logger.info("🚦 Avvio richiesta con Tool Paralleli Disabilitati...");

        return chatClient.prompt()
                .user("Dimmi il meteo di Roma, Milano e Napoli.")
                .tools(new MockWeatherService())
                .options(AnthropicChatOptions.builder()
                        // Usiamo AUTO (decide lui), ma passiamo 'true' per disabilitare l'esecuzione parallela!
                        .toolChoice(new AnthropicApi.ToolChoiceAuto(true))
                        .build())
                .call()
                .content();
    }

}