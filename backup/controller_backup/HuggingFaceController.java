package backup.controller_backup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

//@RestController
@RequestMapping("/ai/huggingface")
public class HuggingFaceController {

    private static final Logger logger = LoggerFactory.getLogger(HuggingFaceController.class);
    private final ChatClient chatClient;

    public HuggingFaceController(@Qualifier("huggingfaceChatModel") ChatModel chatModel) {
        this.chatClient = ChatClient.create(chatModel);
    }

    /**
     * ESEMPIO 1: Chiamata Base
     * A differenza degli altri provider, qui non passiamo "Options" (come la temperature o il nome del modello)
     * perché quelle regole sono cablate direttamente nel server TGI di Hugging Face!
     */
    @GetMapping("/domanda-custom")
    public String domandaAlModelloCustom() {
        logger.info("🤗 Avvio Hugging Face verso l'endpoint TGI privato...");

        return chatClient.prompt()
                .user("Spiegami la teoria della relatività come se avessi 5 anni.")
                // Non ci sono .options() specifiche per HF in Spring AI per ora!
                .call()
                .content();
    }
}