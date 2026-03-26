package backup.controller_backup;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/ai/defaults")
public class DefaultsController {

    private final ChatClient dynamicClient;

    public DefaultsController(@Qualifier("dynamicRoleClient") ChatClient dynamicClient) {
        this.dynamicClient = dynamicClient;
    }

    // ==========================================
    // 1. INIEZIONE DEL PARAMETRO DI DEFAULT
    // ==========================================
    @GetMapping("/voice")
    public Map<String, String> dynamicVoice(
            @RequestParam(defaultValue = "Spiegami cos'è l'Intelligenza Artificiale") String message,
            @RequestParam(defaultValue = "Yoda di Star Wars") String voice) {

        String response = dynamicClient.prompt()
                // Riempiamo il placeholder {voice} definito nel ChatConfig
                .system(s -> s.param("voice", voice))
                .user(message)
                .call()
                .content();

        return Map.of(
                "richiesta", message,
                "voce_usata", voice,
                "risposta", response
        );
    }

    // ==========================================
    // 2. OVERRIDE DELLE OPZIONI A RUNTIME
    // ==========================================
    @GetMapping("/override")
    public Map<String, String> overrideOptions(@RequestParam(defaultValue = "Quanto fa 2+2?") String message) {

        String response = dynamicClient.prompt()
                .system(s -> s.param("voice", "un matematico molto noioso"))
                .user(message)
                // OVERRIDE: Il nostro builder aveva temperature=0.8 (creativo).
                // Per un calcolo matematico vogliamo massima precisione, quindi sovrascriviamo
                // le opzioni solo per questa specifica chiamata mettendo temperature=0.0
                .options(OllamaChatOptions.builder()
                        .temperature(0.0)
                        .build())
                .call()
                .content();

        return Map.of("risposta", response);
    }
}