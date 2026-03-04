package spring.ai.example.spring_ai_demo.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.template.st.StTemplateRenderer; // Per cambiare i delimitatori
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai/templates")
public class TemplateController {

    private final ChatClient chatClient;

    public TemplateController(@Qualifier("llamaClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    // ==========================================
    // 1. TEMPLATE BASE (Delimitatori di default: {})
    // ==========================================
    @GetMapping("/basic")
    public String basicTemplate(
            @RequestParam(defaultValue = "John Williams") String composer,
            @RequestParam(defaultValue = "5") int count) {

        // Invece di concatenare stringhe, usiamo .param() per iniettare i valori
        return chatClient.prompt()
                .user(u -> u
                        .text("Dimmi i nomi di {count} film la cui colonna sonora è stata composta da {composer}.")
                        .param("count", count)
                        .param("composer", composer)
                )
                .call()
                .content();
    }

    // ==========================================
    // 2. TEMPLATE CON DELIMITATORI CUSTOM (< >)
    // Utile se il tuo prompt contiene JSON (che usa le {})
    // ==========================================
    @GetMapping("/custom-delimiters")
    public String customDelimitersTemplate(
            @RequestParam(defaultValue = "Sci-Fi") String genre,
            @RequestParam(defaultValue = "Christopher Nolan") String director) {

        // Esempio di prompt che contiene JSON (finto) per far capire all'AI il formato
        String promptText = """
                Dammi un suggerimento di un film di genere <genre> diretto da <director>.
                Rispondi RIGOROSAMENTE usando questo formato JSON:
                {
                   "titolo": "nome del film",
                   "anno": 2000
                }
                """;

        return chatClient.prompt()
                .user(u -> u
                        .text(promptText)
                        .param("genre", genre)
                        .param("director", director)
                )
                // Cambiamo i delimitatori in < e > per non confondere il renderer con le {} del JSON
                .templateRenderer(StTemplateRenderer.builder()
                        .startDelimiterToken('<')
                        .endDelimiterToken('>')
                        .build()
                )
                .call()
                .content();
    }

    // ==========================================
    // 3. SYSTEM TEMPLATE (Template anche per le istruzioni di sistema!)
    // ==========================================
    @GetMapping("/system")
    public String systemTemplate(
            @RequestParam(defaultValue = "pirata") String role,
            @RequestParam(defaultValue = "Come si fa il caffè?") String message) {

        return chatClient.prompt()
                // Possiamo usare i template anche nel System Prompt!
                .system(s -> s
                        .text("Sei un {role}. Rispondi a tutte le domande immedesimandoti in questo ruolo.")
                        .param("role", role)
                )
                .user(message)
                .call()
                .content();
    }
}