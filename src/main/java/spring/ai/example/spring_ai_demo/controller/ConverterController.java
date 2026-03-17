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

    @GetMapping("/lista-film")
    public java.util.List<String> generaListaFilm(@RequestParam(defaultValue = "Christopher Nolan") String regista) {

        logger.info("🎬 Generazione lista film per il regista: {}", regista);

        // 1. Creiamo il Converter specifico per le Liste
        // Usiamo il DefaultConversionService di Spring che sa come gestire le stringhe
        org.springframework.ai.converter.ListOutputConverter listConverter =
                new org.springframework.ai.converter.ListOutputConverter(new org.springframework.core.convert.support.DefaultConversionService());

        // 2. FORMAT PROVIDER: Chiediamo a Spring AI di svelarci le sue istruzioni segrete!
        String istruzioniDiFormato = listConverter.getFormat();

        // Stampiamo le istruzioni in console per pura curiosità didattica
        logger.info("Le istruzioni segrete iniettate nel prompt sono:\n{}", istruzioniDiFormato);

        // 3. PROMPT TEMPLATE: Costruiamo il prompt unendo la nostra domanda alle istruzioni
        String rispostaTestualeGrezza = chatClient.prompt()
                .user(u -> u.text("Dimmi i titoli di 5 film diretti da {regista}.\n\n{formato}")
                        .param("regista", regista)
                        // Iniettiamo le regole di formattazione al posto del placeholder {formato}
                        .param("formato", istruzioniDiFormato))
                .options(ChatOptions.builder().temperature(0.0).build())
                .call()
                .content(); // Questa volta prendiamo il .content() grezzo, non l'Entity!

        logger.info("Risposta testuale grezza dell'IA: {}", rispostaTestualeGrezza);

        // 4. CONVERTER: Trasformiamo la stringa grezza separata da virgole in una List<String> Java
        java.util.List<String> listaFinale = listConverter.convert(rispostaTestualeGrezza);

        logger.info("✅ Conversione in List completata. La lista contiene {} elementi.", listaFinale.size());

        return listaFinale;
    }

    // Aggiungi questo import in cima al file se non c'è:
    // import org.springframework.core.ParameterizedTypeReference;

    // 1. Il nostro nuovo Record: Un oggetto che contiene una Stringa e una Lista di Stringhe!
    public record Filmografia(String regista, java.util.List<String> film) {}

    // 2. L'endpoint per le liste complesse
    @GetMapping("/filmografie-multiple")
    public java.util.List<Filmografia> generaFilmografieMultiple(
            @RequestParam(defaultValue = "Christopher Nolan, Quentin Tarantino") String registi) {

        logger.info("🎬 Generazione filmografie multiple per: {}", registi);

        return chatClient.prompt()
                .user(u -> u.text("Genera una filmografia di esattamente 3 film famosi per ciascuno di questi registi: {registi}.")
                        .param("registi", registi))
                // Teniamo alta la memoria dei Token perché il JSON in uscita sarà lungo!
                .options(ChatOptions.builder().temperature(0.0).maxTokens(800).build())
                .call()
                // 3. LA MAGIA: Usiamo il ParameterizedTypeReference per chiedere una List<Filmografia>
                .entity(new org.springframework.core.ParameterizedTypeReference<java.util.List<Filmografia>>() {});
    }

    @GetMapping("/crea-mostro")
    public java.util.Map<String, Object> creaMostro(
            @RequestParam(defaultValue = "Un drago anziano che vive in un vulcano e sputa lava") String descrizione) {

        logger.info("🐉 Creazione statistiche dinamiche per: {}", descrizione);

        return chatClient.prompt()
                .user(u -> u.text("Sei il Game Master di un gioco di ruolo. Leggi questa descrizione: <desc> \n" +
                                "Genera le statistiche del personaggio. Inventa liberamente i nomi delle chiavi (es. punti_vita, attacco_fuoco, carisma, debolezza_principale, ecc.) " +
                                "in base a ciò che ha più senso per la descrizione.")
                        .param("desc", descrizione))
                // Usiamo i delimitatori custom per sicurezza, come abbiamo imparato!
                .templateRenderer(StTemplateRenderer.builder().startDelimiterToken('<').endDelimiterToken('>').build())
                // Usiamo la Temperatura un po' più alta (0.4) per fargli inventare chiavi creative, e maxTokens alti per non farlo mozzare!
                .options(ChatOptions.builder().temperature(0.4).maxTokens(500).build())
                .call()
                // LA MAGIA DELLA MAPPA: Chiediamo un JSON dinamico e destrutturato
                .entity(new org.springframework.core.ParameterizedTypeReference<java.util.Map<String, Object>>() {});
    }

    @GetMapping("/lista-gelati")
    public java.util.List<String> generaListaGelati(@RequestParam(defaultValue = "gusti di gelato strani e creativi") String soggetto) {

        logger.info("🍦 Generazione lista per: {}", soggetto);

        return chatClient.prompt()
                .user(u -> u.text("Elenca 5 {soggetto}.")
                        .param("soggetto", soggetto))
                .options(ChatOptions.builder().temperature(0.5).build())
                .call()
                // ECCO LA MAGIA DELL'API FLUIDA:
                // Fa tutto lui: inietta le regole, chiama il modello, e converte l'output testuale separato da virgole in una List<String> Java!
                .entity(new org.springframework.ai.converter.ListOutputConverter(new org.springframework.core.convert.support.DefaultConversionService()));
    }
}