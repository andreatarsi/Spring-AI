package backup.controller_backup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux; // <-- Import fondamentale per lo streaming!

@RestController
@RequestMapping("/ai/stream")
public class StreamingController {

    private static final Logger logger = LoggerFactory.getLogger(StreamingController.class);
    private final ChatClient chatClient;

    // Usiamo il nostro client di base (quello testuale, non quello visivo)
    public StreamingController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    // Usiamo produces = TEXT_EVENT_STREAM_VALUE per dire al browser di non aspettare la fine,
    // ma di stampare i dati appena arrivano!
    @GetMapping(value = "/storia", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> generaStoria(@RequestParam(defaultValue = "un programmatore Java che scopre l'IA") String argomento) {

        logger.info("🌊 Avvio stream generativo per: {}", argomento);

        // Invece di .call().content(), usiamo .stream().content() !
        return chatClient.prompt()
                .user("Scrivi una breve storia avvincente (circa 100 parole) su: " + argomento)
                .stream() // <--- ATTIVA LO STREAMING CHAT MODEL (Dal diagramma!)
                .content()
                // IL TRUCCO: Proteggiamo gli spazi e gli 'a capo' trasformandoli in codice HTML
                // prima che il protocollo di rete li faccia sparire!
                .map(pezzo -> pezzo.replace(" ", "&nbsp;").replace("\n", "<br>"))
                .doOnNext(pezzo -> {
                    // Stampiamo in console ogni singolo "token" (pezzo di parola) man mano che viene generato
                    System.out.print(pezzo);
                })
                .doOnComplete(() -> {
                    // Quando il flusso è finito, andiamo a capo nel log
                    System.out.println("\n✅ Flusso completato!");
                });
    }
}