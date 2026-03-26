package backup.controller_backup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel; // <-- Nota l'import!
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.beans.factory.annotation.Qualifier; // <-- Nota l'import!
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

//@RestController
@RequestMapping("/ai/zhipu")
public class ZhiPuController {

    private static final Logger logger = LoggerFactory.getLogger(ZhiPuController.class);
    private final ChatClient chatClient;
    private final ImageModel imageModel;

    // REFACTORING: Chiediamo a Spring i modelli ESATTI di ZhiPu
    public ZhiPuController(
            @Qualifier("zhiPuAiChatModel") ChatModel chatModel,
            @Qualifier("zhiPuAiImageModel") ImageModel imageModel) {

        // Creiamo il client internamente usando il motore specifico!
        this.chatClient = ChatClient.create(chatModel);
        this.imageModel = imageModel;
    }

    @GetMapping("/enigma-logico")
    public String risolviEnigma() {
        return chatClient.prompt().user("Indovinello: ...").options(ZhiPuAiChatOptions.builder().model("glm-4-plus").build()).call().content();
    }

    @GetMapping("/cyber-panda")
    public String dipingiPanda() {
        ImageResponse response = imageModel.call(new ImagePrompt("Panda rosso cyberpunk, 4k"));
        return "<img src='" + response.getResult().getOutput().getUrl() + "'/>";
    }
}