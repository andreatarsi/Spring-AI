package spring.ai.example.spring_ai_demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// @RestController  <-- Nascondiamo QianFan perché il plugin della community è rotto con Spring Boot 4
@RequestMapping("/ai/qianfan")
public class QianFanController {

    private static final Logger logger = LoggerFactory.getLogger(QianFanController.class);
    private final ChatClient chatClient;

    // REFACTORING: Chiediamo a Spring il motore ESATTO di QianFan
    public QianFanController(@Qualifier("qianFanChatModel") ChatModel chatModel) {
        this.chatClient = ChatClient.create(chatModel);
    }

    @GetMapping("/saggezza-orientale")
    public String saggezzaBaidu() {
        return chatClient.prompt().user("Parlami della filosofia del tè.").call().content();
    }
}