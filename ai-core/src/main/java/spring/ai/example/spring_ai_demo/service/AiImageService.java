package spring.ai.example.spring_ai_demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImageOptionsBuilder;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AiImageService {

    private static final Logger log = LoggerFactory.getLogger(AiImageService.class);

    // Mappa di tutti i generatori di immagini disponibili!
    private final Map<String, ImageModel> imageModels;

    public AiImageService(Map<String, ImageModel> imageModels) {
        this.imageModels = imageModels;
    }

    // Aggiunto il parametro 'requestedModel' per l'Omni-Payload!
    public String generateImage(String promptText, String requestedModel) {

        String engineKey = (requestedModel != null && !requestedModel.isBlank())
                ? requestedModel.toLowerCase()
                : "openai"; // DALL-E come default storico

        String activeEngine = switch (engineKey) {
            case "openai", "dalle", "dall-e-3" -> "OpenAiImageModel";
            case "stability", "stabilityai" -> "StabilityAiImageModel"; // Per il futuro
            default -> "OpenAiImageModel";
        };

        ImageModel activeModel = imageModels.get(activeEngine);

        if (activeModel == null) {
            throw new IllegalStateException("Motore Immagini non trovato o disabilitato: " + activeEngine + ". Modelli disponibili: " + imageModels.keySet());
        }

        log.info("🎨 Richiesta generazione immagine su [{}]: '{}'", activeEngine, promptText);

        var options = ImageOptionsBuilder.builder()
                .N(1)
                .height(1024)
                .width(1024)
                .build();

        ImagePrompt imagePrompt = new ImagePrompt(promptText, options);
        ImageResponse response = activeModel.call(imagePrompt);

        return response.getResult().getOutput().getUrl();
    }
}