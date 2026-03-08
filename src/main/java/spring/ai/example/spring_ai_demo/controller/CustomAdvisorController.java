package spring.ai.example.spring_ai_demo.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import spring.ai.example.spring_ai_demo.advisor.PaninoAdvisor; // Importiamo il nostro Advisor

@RestController
@RequestMapping("/ai/custom-advisor")
public class CustomAdvisorController {

    private final ChatClient chatClient;

    public CustomAdvisorController(@Qualifier("llamaClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/test")
    public String testPanino(@RequestParam(defaultValue = "Spiegami cos'è Java") String message) {
        return chatClient.prompt()
                .user(message)
                // Inseriamo il nostro Advisor nella catena!
                .advisors(new PaninoAdvisor())
                .call()
                .content();
    }
}