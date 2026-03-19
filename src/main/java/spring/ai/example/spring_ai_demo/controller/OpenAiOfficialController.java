package spring.ai.example.spring_ai_demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.openaisdk.OpenAiSdkChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ai.chat.model.ChatModel;

//@RestController
@RequestMapping("/ai/official")
public class OpenAiOfficialController {

    private static final Logger logger = LoggerFactory.getLogger(OpenAiOfficialController.class);
    private final ChatClient chatClient;

    // REFACTORING: Chiediamo esplicitamente il motore di OpenAI
    public OpenAiOfficialController(@Qualifier("openAiChatModel") ChatModel chatModel) {
        // Creiamo il ChatClient "isolato"
        this.chatClient = ChatClient.create(chatModel);
    }

    /**
     * PIATTO 1: Il Modello Ragionante (o1 / o3)
     * Usiamo il parametro esclusivo "maxCompletionTokens" per permettere all'IA
     * di "pensare" a lungo prima di darci la soluzione matematica.
     */
    @GetMapping("/ragiona")
    public String matematicaComplessa() {
        logger.info("🧠 Avvio OpenAI SDK con modello ragionante (o1-preview)...");

        return chatClient.prompt()
                .user("Risolvi questa equazione complessa spiegando ogni passaggio logico: 3x^2 - 12x + 9 = 0")
                .options(OpenAiSdkChatOptions.builder()
                        .model("o1-preview")
                        // TRUCCO: I modelli 'o1' rifiutano maxTokens, vogliono maxCompletionTokens!
                        .maxCompletionTokens(1500)
                        .build())
                .call()
                .content();
    }

    /**
     * PIATTO 2: La Voce dell'IA (Audio in Output)
     * Questo endpoint non restituisce testo, ma restituisce letteralmente
     * un file audio WAV scaricabile o riproducibile nel browser!
     */
    @GetMapping(value = "/parla", produces = "audio/wav")
    public byte[] generaVoce() {
        logger.info("🎙️ Avvio OpenAI SDK per generazione vocale nativa...");

        ChatResponse response = chatClient.prompt()
                .user("Raccontami una barzelletta sui programmatori Java in italiano.")
                .options(OpenAiSdkChatOptions.builder()
                        .model("gpt-4o-audio-preview")
                        .outputModalities(java.util.List.of("text", "audio")) // Vogliamo l'audio!
                        // Scegliamo la voce "ALLOY" e il formato "WAV"
                        .outputAudio(new OpenAiSdkChatOptions.AudioParameters(
                                OpenAiSdkChatOptions.AudioParameters.Voice.ALLOY,
                                OpenAiSdkChatOptions.AudioParameters.AudioResponseFormat.WAV))
                        .build())
                .call()
                .chatResponse();

        // Estraiamo i byte grezzi dell'audio dalla risposta
        return response.getResult().getOutput().getMedia().get(0).getDataAsByteArray();
    }

    /**
     * PIATTO 3: Integrazione GitHub Models (Dev-Friendly)
     * Usiamo i server gratuiti di GitHub passando il nostro Personal Access Token.
     */
    @GetMapping("/github")
    public String usaGitHubModels() {
        logger.info("🐙 Avvio OpenAI SDK agganciato ai server di GitHub Models...");

        return chatClient.prompt()
                .user("Qual è la differenza tra Git e GitHub?")
                .options(OpenAiSdkChatOptions.builder()
                        .baseUrl("https://models.inference.ai.azure.com")
                        .apiKey("IL_TUO_GITHUB_PAT_TOKEN") // La chiave presa da GitHub!
                        .model("gpt-4o")
                        .build())
                .call()
                .content();
    }

    /**
     * PIATTO 4: Azure Passwordless (La Sicurezza Enterprise Massima)
     * (Avvertenza: Fallirà in locale senza Azure CLI configurata, ma è perfetto in produzione)
     */
    @GetMapping("/azure-sicuro")
    public String usaAzureEnterprise() {
        logger.info("☁️ Avvio OpenAI SDK verso Azure in modalità Passwordless...");

        return chatClient.prompt()
                .user("Fammi un riassunto dei principi SOLID.")
                .options(OpenAiSdkChatOptions.builder()
                        .baseUrl("https://IL_TUO_NOME_AZURE.openai.azure.com")
                        .deploymentName("gpt-4-aziendale")
                        .azure(true) // Attiviamo la modalità Azure
                        // NOTA: Non passiamo NESSUNA apiKey! L'SDK chiederà i permessi
                        // direttamente al server Azure su cui sta girando l'app.
                        .build())
                .call()
                .content();
    }
}