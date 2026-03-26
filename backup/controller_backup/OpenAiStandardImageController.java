package backup.controller_backup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.image.ImageModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;


/**
 * Controller per la generazione di immagini tramite OpenAI (Implementazione Standard).
 * Ottimizzato per gestire i retry automatici in caso di errore 429 (Rate Limit).
 */
// @RestController
@RequestMapping("/ai/openai-standard-image")
public class OpenAiStandardImageController {

    private static final Logger logger = LoggerFactory.getLogger(OpenAiStandardImageController.class);

    private final ImageModel openAiImageModel;

    public OpenAiStandardImageController(@Qualifier("openAiImageModel") ImageModel openAiImageModel) {
        this.openAiImageModel = openAiImageModel;
    }

    /*
    @GetMapping("/generate")
    public Map<String, Object> generate(@RequestParam(defaultValue = "Un gatto samurai dipinto ad acquerello") String prompt) {
        logger.info("Generazione immagine standard OpenAI in corso...");

        ImageResponse response = openAiImageModel.call(
                new ImagePrompt(prompt,
                        OpenAiImageOptions.builder()
                                .model("dall-e-3")
                                .quality("standard") // 'hd' o 'standard'
                                .style("natural")   // 'vivid' o 'natural'
                                .N(1)               // DALL-E 3 supporta solo 1 immagine alla volta
                                .build())
        );

        return Map.of(
                "prompt", prompt,
                "url", response.getResult().getOutput().getUrl(),
                "provider", "OpenAI Standard Implementation"
        );
    }
    */
}