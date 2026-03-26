package backup.controller_backup;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class BonusController {

    private final ChatClient chatClientConMemoria;

    public BonusController(ChatClient.Builder builder) {

        // 1. Creiamo il "Database" in memoria per le chat
        InMemoryChatMemoryRepository repository = new InMemoryChatMemoryRepository();

        // 2. Definiamo la "Finestra": ricordati solo gli ultimi 10 messaggi!
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(repository)
                .maxMessages(10) // Salvavita per non prosciugare il budget dei token!
                .build();

        // 3. Costruiamo il client iniettando l'Advisor usando il BUILDER!
        this.chatClientConMemoria = builder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    @GetMapping("/chat-con-memoria")
    public String chat(@RequestParam String messaggio, @RequestParam String chatId) {
        return chatClientConMemoria.prompt()
                .user(messaggio)
                // Usiamo la stringa esatta richiesta da Spring AI per l'ID conversazione
                .advisors(a -> a.param("chat_memory_conversation_id", chatId))
                .call()
                .content();
    }

    // Definiamo la struttura dati che vogliamo indietro
    record Persona(String nome, String cognome, int eta) {}

    @GetMapping("/estrai-persone")
    public List<Persona> estraiPersone() {

        // Non restituiamo più una Stringa, ma una vera Lista di oggetti Java!
        return chatClientConMemoria.prompt()
                .user("Inventa 3 personaggi di un romanzo fantasy con nome, cognome ed età.")
                .call()
                // MAGIA: Diciamo a Spring AI di convertire il testo dell'IA in List<Persona>
                .entity(new ParameterizedTypeReference<List<Persona>>() {});
    }

    @GetMapping("/traduttore-dinamico")
    public String traduci(@RequestParam String testo, @RequestParam String lingua) {

        // Creiamo il template con le variabili tra parentesi graffe
        String templateString = "Sei un traduttore esperto. Traduci la seguente frase '{testo_da_tradurre}' nella lingua '{lingua_target}'.";
        PromptTemplate promptTemplate = new PromptTemplate(templateString);

        // Iniettiamo le variabili in modo sicuro
        Prompt prompt = promptTemplate.create(Map.of(
                "testo_da_tradurre", testo,
                "lingua_target", lingua
        ));

        return chatClientConMemoria.prompt(prompt).call().content();
    }
}