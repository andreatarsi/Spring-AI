package spring.ai.example.spring_ai_demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.template.st.StTemplateRenderer; // <-- L'IMPORT MAGICO CHE MANCAVA!
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai/converter")
public class ConverterController {

    private static final Logger logger = LoggerFactory.getLogger(ConverterController.class);
    private final ChatClient chatClient;

    public ConverterController(@Qualifier("llamaClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public record Recensione(String titolo, int voto, String riassunto) {}

    @GetMapping("/recensione")
    public Recensione generaRecensione(@RequestParam(defaultValue = "Inception") String film) {

        int maxTentativi = 3;

        for (int tentativo = 1; tentativo <= maxTentativi; tentativo++) {
            try {
                logger.info("🎬 Tentativo {}/{} - Chiedo la recensione strutturata per: {}", tentativo, maxTentativi, film);

                Recensione recensioneGenerata = chatClient.prompt()
                        .user(u -> u.text("Scrivi una breve recensione per il film <film>. " +
                                        "Devi rispondere ESCLUSIVAMENTE con un JSON valido che termini con la parentesi graffa chiusa. " +
                                        "Esempio perfetto:\n" +
                                        "{\n" +
                                        "  \"titolo\": \"Nome Film\",\n" +
                                        "  \"voto\": 9,\n" +
                                        "  \"riassunto\": \"Testo del riassunto.\"\n" +
                                        "}")
                                .param("film", film))
                        // Usiamo i delimitatori < > per non litigare col JSON
                        .templateRenderer(StTemplateRenderer.builder()
                                .startDelimiterToken('<')
                                .endDelimiterToken('>')
                                .build())
                        .options(ChatOptions.builder()
                                .temperature(0.2)
                                .maxTokens(500)
                                .build())
                        .call()
                        .entity(Recensione.class);

                logger.info("✅ Conversione Riuscita! Titolo='{}', Voto={}/10", recensioneGenerata.titolo(), recensioneGenerata.voto());
                return recensioneGenerata;

            } catch (Exception e) {
                logger.warn("⚠️ Fallimento al tentativo {}: Il JSON non era valido.", tentativo);

                if (tentativo == maxTentativi) {
                    logger.error("❌ Impossibile ottenere un JSON valido. Attivo la procedura di emergenza (Fallback).");
                    return new Recensione("Errore di Sistema", 0, "I sistemi IA sono attualmente sovraccarichi o non riescono a completare la richiesta. Riprova più tardi.");
                }
            }
        }

        return null;
    }
}