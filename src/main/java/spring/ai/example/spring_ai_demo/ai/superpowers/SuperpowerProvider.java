package spring.ai.example.spring_ai_demo.ai.superpowers;

import org.springframework.ai.chat.prompt.ChatOptions;

public interface SuperpowerProvider {
    // Riceve le richieste dell'utente e restituisce le Opzioni specifiche del modello
    ChatOptions getOptions(Boolean webSearch, Boolean deepThinking);
}