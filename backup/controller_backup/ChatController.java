package backup.controller_backup;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import backup.service.MultiModelService;

@RestController
public class ChatController {

    private final ChatClient llamaClient;
    private final ChatClient mistralClient;
    private final ChatClient comedianClient;
    private final ChatClient teacherClient;
    private final MultiModelService multiModelService;

    // Aggiorna il costruttore per includere il nuovo service
    public ChatController(
            @Qualifier("llamaClient") ChatClient llamaClient,
            @Qualifier("mistralClient") ChatClient mistralClient,
            @Qualifier("comedianClient") ChatClient comedianClient,
            @Qualifier("teacherClient") ChatClient teacherClient,
            MultiModelService multiModelService) { // <-- Nuova iniezione

        this.llamaClient = llamaClient;
        this.mistralClient = mistralClient;
        this.comedianClient = comedianClient;
        this.teacherClient = teacherClient;
        this.multiModelService = multiModelService; // <-- Assegnazione
    }

    // ... I tuoi endpoint precedenti ...

    @GetMapping("/ai/llama")
    public String askLlama(@RequestParam(defaultValue = "Ciao") String message) {
        return llamaClient.prompt(message).call().content();
    }

    @GetMapping("/ai/mistral")
    public String askMistral(@RequestParam(defaultValue = "Ciao") String message) {
        return mistralClient.prompt(message).call().content();
    }

    // ... I nuovi endpoint con le personalità ...

    @GetMapping("/ai/comedian")
    public String askComedian(@RequestParam(defaultValue = "Spiegami cos'è Java") String message) {
        return comedianClient.prompt(message).call().content();
    }

    @GetMapping("/ai/teacher")
    public String askTeacher(@RequestParam(defaultValue = "Spiegami cos'è Java") String message) {
        return teacherClient.prompt(message).call().content();
    }

    @GetMapping("/ai/mutate-test")
    public String runMutateTest() {
        return multiModelService.testMutateFlow();
    }
}