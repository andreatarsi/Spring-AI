package spring.ai.example.spring_ai_demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
// import org.springframework.ai.openai.OpenAiSdkImageOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller per la generazione di immagini tramite OpenAI Official SDK.
 * Supporta feature avanzate di DALL-E 3 come Style e Quality.
 */
// @RestController
@RequestMapping("/ai/openai-sdk-image")
public class OpenAiSdkImageController {

    private static final Logger logger = LoggerFactory.getLogger(OpenAiSdkImageController.class);

    private final ImageModel sdkImageModel;

    // Specifichiamo il bean dell'SDK ufficiale
    public OpenAiSdkImageController(@Qualifier("openAiImageModel") ImageModel sdkImageModel) {
        this.sdkImageModel = sdkImageModel;
    }

    /*
    @GetMapping("/generate")
    public Map<String, Object> generate(@RequestParam(defaultValue = "Un castello steampunk tra le nuvole") String prompt) {
        logger.info("Richiesta generazione immagine via OpenAI SDK...");

        ImageResponse response = sdkImageModel.call(
                new ImagePrompt(prompt,
                        OpenAiSdkImageOptions.builder()
                                .model("dall-e-3")
                                .quality("hd")    // Alta definizione
                                .style("vivid")   // Colori vibranti e drammatici
                                .width(1024)
                                .height(1024)
                                .build())
        );

        return Map.of(
                "prompt", prompt,
                "url", response.getResult().getOutput().getUrl(),
                "metadata", response.getMetadata()
        );
    }
    */
}