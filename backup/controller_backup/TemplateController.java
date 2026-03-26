package backup.controller_backup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/ai/templates")
public class TemplateController {

    // 1. Aggiungiamo il Logger!
    private static final Logger logger = LoggerFactory.getLogger(TemplateController.class);

    private final ChatClient chatClient;

    @Value("classpath:/prompts/storia.st")
    private Resource templateStorie;

    public TemplateController(@Qualifier("llamaClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    // ... [Gli endpoint /basic, /custom-delimiters e /system restano identici a prima] ...

    @GetMapping("/basic")
    public String basicTemplate(
            @RequestParam(defaultValue = "John Williams") String composer,
            @RequestParam(defaultValue = "5") int count) {
        return chatClient.prompt()
                .user(u -> u.text("Dimmi i nomi di {count} film la cui colonna sonora è stata composta da {composer}.").param("count", count).param("composer", composer))
                .call().content();
    }

    @GetMapping("/custom-delimiters")
    public String customDelimitersTemplate(
            @RequestParam(defaultValue = "Sci-Fi") String genre,
            @RequestParam(defaultValue = "Christopher Nolan") String director) {
        String promptText = "Dammi un suggerimento di un film di genere <genre> diretto da <director>.\nRispondi RIGOROSAMENTE usando questo formato JSON:\n{\n   \"titolo\": \"nome del film\",\n   \"anno\": 2000\n}\n";
        return chatClient.prompt()
                .user(u -> u.text(promptText).param("genre", genre).param("director", director))
                .templateRenderer(StTemplateRenderer.builder().startDelimiterToken('<').endDelimiterToken('>').build())
                .call().content();
    }

    @GetMapping("/system")
    public String systemTemplate(
            @RequestParam(defaultValue = "pirata") String role,
            @RequestParam(defaultValue = "Come si fa il caffè?") String message) {
        return chatClient.prompt()
                .system(s -> s.text("Sei un {role}. Rispondi a tutte le domande immedesimandoti in questo ruolo.").param("role", role))
                .user(message)
                .call().content();
    }

    // ==========================================
    // 4. L'ENDPOINT CORRETTO PER LA STORIA
    // ==========================================
    @GetMapping("/storia")
    public String generaStoria(
            @RequestParam(defaultValue = "Andrea") String nome,
            @RequestParam(defaultValue = "pappagallo saggio") String animale,
            @RequestParam(defaultValue = "comico e divertente") String stile) {

        // 2. Stampiamo un log per essere sicuri di essere arrivati qui!
        logger.info("🏴‍☠️ Preparazione della storia per il capitano {} e il suo {}...", nome, animale);

        SystemPromptTemplate promptTemplate = new SystemPromptTemplate(templateStorie);

        Message systemMessage = promptTemplate.createMessage(Map.of(
                "nome", nome,
                "animale", animale,
                "stile", stile
        ));

        return chatClient.prompt()
                .messages(systemMessage)
                // 3. LA SCINTILLA: Diamo a Mistral il comando "User" per farlo partire!
                .user("Inizia a raccontare la storia adesso!")
                .call()
                .content();
    }
}