package spring.ai.example.spring_ai_demo.service;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.DefaultToolCallingManager;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.method.MethodToolCallback;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import spring.ai.example.spring_ai_demo.config.WeatherToolsConfig;
import spring.ai.example.spring_ai_demo.tools.DateTimeTools;

import java.util.ArrayList;
import java.util.List;

@Service
public class ToolCallingService {

    private final ChatModel chatModel;
    private final List<ToolCallback> allTools = new ArrayList<>();

    public ToolCallingService(ChatModel chatModel,
                              DateTimeTools dateTimeTools,
                              WeatherToolsConfig weatherTools) {
        this.chatModel = chatModel;

        java.lang.reflect.Method timeMethod = ReflectionUtils
                .findMethod(DateTimeTools.class, "getCurrentDateTime");

        java.lang.reflect.Method weatherMethod = ReflectionUtils
                .findMethod(WeatherToolsConfig.class, "currentWeather", String.class);

        this.allTools.add(MethodToolCallback.builder()
                .toolObject(dateTimeTools)
                .toolMethod(timeMethod)
                .toolDefinition(ToolDefinition.builder()
                        .name("getCurrentDateTime")
                        .description("Ottiene la data e l'ora attuale")
                        // SCHEMA SEMPLIFICATO
                        .inputSchema("{\"type\": \"object\"}")
                        .build())
                .build());

        this.allTools.add(MethodToolCallback.builder()
                .toolObject(weatherTools)
                .toolMethod(weatherMethod)
                .toolDefinition(ToolDefinition.builder()
                        .name("currentWeather")
                        .description("Ottiene il meteo attuale per una città")
                        .inputSchema("""
                                {
                                    "type": "object",
                                    "properties": {
                                        "location": { "type": "string", "description": "Il nome della città" }
                                    },
                                    "required": ["location"]
                                }
                                """)
                        .build())
                .build());
    }

    public String askWithTools(String message) {
        // Aggiungi questa riga:
        System.out.println("🤖 --- MODELLO IN USO: " + chatModel.getClass().getSimpleName() + " ---");
        List<Message> messages = new ArrayList<>();

        messages.add(new SystemMessage("""
                Sei un assistente utile e conciso.
                REGOLE:
                1. Rispondi alle domande dell'utente usando ESCLUSIVAMENTE i tool a tua disposizione, se applicabili.
                2. Non inventare mai dati come l'ora o il meteo.
                """));

        messages.add(new UserMessage(message));

        var chatOptions = ToolCallingChatOptions.builder()
                .toolCallbacks(allTools)
                .internalToolExecutionEnabled(false)
                .build();

        var prompt = new Prompt(messages, chatOptions);
        ChatResponse chatResponse = chatModel.call(prompt);

        System.out.println("--- RAW OUTPUT: " + chatResponse.getResult().getOutput().getText() + " ---");
        System.out.println("--- HAS TOOL CALLS: " + chatResponse.hasToolCalls() + " ---");

        while (chatResponse != null && chatResponse.hasToolCalls()) {
            var toolCallingManager = DefaultToolCallingManager.builder().build();
            var toolExecutionResult = toolCallingManager.executeToolCalls(prompt, chatResponse);

            if (toolExecutionResult.returnDirect()) {
                var history = toolExecutionResult.conversationHistory();
                String directResult = history.get(history.size() - 1).getText();
                System.out.println("--- RETURN DIRECT: " + directResult + " ---");
                return directResult != null ? directResult : "Nessuna risposta disponibile";
            }

            prompt = new Prompt(toolExecutionResult.conversationHistory(), chatOptions);
            chatResponse = chatModel.call(prompt);

            System.out.println("--- POST-TOOL OUTPUT: " + chatResponse.getResult().getOutput().getText() + " ---");
            System.out.println("--- POST-TOOL HAS TOOL CALLS: " + chatResponse.hasToolCalls() + " ---");
        }

        if (chatResponse == null || chatResponse.getResult() == null) {
            return "Errore: risposta nulla dal modello";
        }

        String finalText = chatResponse.getResult().getOutput().getText();
        System.out.println("--- FINAL TEXT: " + finalText + " ---");

        return finalText != null ? finalText : "Nessuna risposta disponibile";
    }
}