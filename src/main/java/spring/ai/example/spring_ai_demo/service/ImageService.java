package spring.ai.example.spring_ai_demo.service;

import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.image.ImageOptionsBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ImageService {

    private final ImageModel imageModel;

    // Spring inietterà il modello di immagine attivo (es. OpenAI o StabilityAI)
    // Usiamo il Qualifier per decidere quale dei 4 usare di default
    public ImageService(@Qualifier("openAiImageModel") ImageModel imageModel) {
        this.imageModel = imageModel;
    }

    public ImageResponse generaImmagine(String prompt, int width, int height) {
        // Creiamo il prompt con le opzioni portatili (valide per quasi tutti i provider)
        ImagePrompt imagePrompt = new ImagePrompt(prompt,
                ImageOptionsBuilder.builder()
                        .width(width)
                        .height(height)
                        .width(1) // Chiediamo 1 sola variante
                        .build());

        return imageModel.call(imagePrompt);
    }
}