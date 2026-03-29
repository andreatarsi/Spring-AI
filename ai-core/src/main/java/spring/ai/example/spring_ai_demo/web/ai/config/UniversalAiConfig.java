package spring.ai.example.spring_ai_demo.web.ai.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.image.ImageModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class UniversalAiConfig {

    private static final Logger log = LoggerFactory.getLogger(UniversalAiConfig.class);

    // Rimuoviamo ChatClient.Builder dai parametri.
    // Ci basta avere la lista dei modelli!
    @Bean
    public Map<String, ChatClient> chatClientMap(List<ChatModel> availableModels) {
        Map<String, ChatClient> clients = new HashMap<>();

        for (ChatModel model : availableModels) {
            String modelName = model.getClass().getSimpleName();
            log.info("🎯 Registrazione ChatClient per: {}", modelName);

            // Usiamo il metodo statico 'builder(model)' per creare un costruttore
            // esatto e dedicato a QUESTO specifico modello del ciclo.
            ChatClient client = ChatClient.builder(model)
                    .defaultSystem("Sei un assistente utile e conciso.") // Default base
                    .build();

            clients.put(modelName, client);
        }

        return clients;
    }

    @Bean
    public Map<String, ImageModel> imageModelMap(List<ImageModel> availableModels) {
        Map<String, ImageModel> models = new HashMap<>();
        for (ImageModel model : availableModels) {
            String modelName = model.getClass().getSimpleName();
            log.info("🎨 Registrazione ImageModel per: {}", modelName);
            models.put(modelName, model);
        }
        return models;
    }

    @Bean
    public Map<String, TranscriptionModel> transcriptionModelMap(List<TranscriptionModel> availableModels) {
        Map<String, TranscriptionModel> models = new HashMap<>();
        for (TranscriptionModel model : availableModels) {
            String modelName = model.getClass().getSimpleName();
            log.info("🎙️ Registrazione TranscriptionModel per: {}", modelName);
            models.put(modelName, model);
        }
        return models;
    }
}