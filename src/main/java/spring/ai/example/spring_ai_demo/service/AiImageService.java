package spring.ai.example.spring_ai_demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImageOptionsBuilder;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

@Service
public class AiImageService {

    private static final Logger log = LoggerFactory.getLogger(AiImageService.class);

    // Usiamo ObjectProvider perché il bean ImageModel potrebbe non esistere
    // se avviamo un profilo che fa solo testo (es. Ollama senza LLaVA)
    private final ObjectProvider<ImageModel> imageModelProvider;

    public AiImageService(ObjectProvider<ImageModel> imageModelProvider) {
        this.imageModelProvider = imageModelProvider;
    }

    public String generateImage(String promptText) {
        ImageModel imageModel = imageModelProvider.getIfAvailable();

        if (imageModel == null) {
            throw new IllegalStateException("Nessun motore di generazione immagini configurato o attivo in questo profilo!");
        }

        log.info("Richiesta generazione immagine: '{}'", promptText);

        // Configuriamo opzioni standard (es. 1 immagine, risoluzione base)
        var options = ImageOptionsBuilder.builder()
                .N(1)
                .height(1024)
                .width(1024)
                .build();

        ImagePrompt imagePrompt = new ImagePrompt(promptText, options);

        // Eseguiamo la chiamata
        ImageResponse response = imageModel.call(imagePrompt);

        // Restituiamo l'URL o il Base64 dell'immagine generata
        return response.getResult().getOutput().getUrl();
    }
}