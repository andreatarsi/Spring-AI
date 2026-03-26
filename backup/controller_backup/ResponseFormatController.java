package backup.controller_backup;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/ai/responses")
public class ResponseFormatController {

    private final ChatClient chatClient;

    public ResponseFormatController(@Qualifier("llamaClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    // ==========================================
    // 1. METADATI E CHATRESPONSE
    // ==========================================
    @GetMapping("/metadata")
    public Map<String, Object> getMetadata(@RequestParam(defaultValue = "Qual è la capitale d'Italia?") String message) {
        // Chiamiamo .chatResponse() invece di .content()
        ChatResponse response = chatClient.prompt(message).call().chatResponse();

        assert response != null;
        String content = Objects.requireNonNull(response.getResult()).getOutput().getText();
        // Estraiamo l'uso dei token (utile per calcolare i costi, anche se Ollama è gratis!)
        var usage = response.getMetadata().getUsage();

        assert content != null;
        return Map.of(
                "rispostaTestuale", content,
                "tokenTotaliConsumati", usage.getTotalTokens()
        );
    }

    // ==========================================
    // 2. STRUCTURED OUTPUT (Entità singola)
    // ==========================================

    // Definiamo un Record (novità fantastica di Java 14+) per mappare i dati
    public record ActorFilms(String attore, List<String> film) {}

    @GetMapping("/entity")
    public ActorFilms getEntity() {
        // Spring AI chiederà al modello di rispondere in JSON e lo mapperà nel nostro Record Java!
        return chatClient.prompt("Genera la filmografia di un attore famoso a tua scelta.")
                .call()
                .entity(ActorFilms.class);
    }

    // ==========================================
    // 3. STRUCTURED OUTPUT (Lista di Entità)
    // ==========================================
    @GetMapping("/list")
    public List<ActorFilms> getEntityList() {
        // Usiamo ParameterizedTypeReference per mappare i Generics di Java (List<T>)
        return chatClient.prompt("Genera la filmografia di 3 attori italiani famosi. Restituisci solo i dati strutturati.")
                .call()
                .entity(new ParameterizedTypeReference<>() {
                });
    }

    // ==========================================
    // 4. STREAMING (Risposta asincrona in tempo reale)
    // ==========================================
    // Impostiamo il MediaType su TEXT_EVENT_STREAM_VALUE per far capire al browser che i dati arriveranno a pezzi
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamResponse(@RequestParam(defaultValue = "Scrivi una fiaba di 10 righe su un drago.") String message) {
        // Usiamo .stream() invece di .call()
        return chatClient.prompt(message)
                .stream()
                .content();
    }
}