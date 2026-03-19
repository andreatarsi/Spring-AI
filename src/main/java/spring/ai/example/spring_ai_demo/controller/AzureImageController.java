package spring.ai.example.spring_ai_demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
// import org.springframework.ai.azure.openai.AzureOpenAiImageOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller per la generazione di immagini tramite Azure OpenAI.
 */
// @RestController
@RequestMapping("/ai/azure-image")
public class AzureImageController {

    private static final Logger logger = LoggerFactory.getLogger(AzureImageController.class);

    private final ImageModel azureImageModel;

    public AzureImageController(@Qualifier("azureOpenAiImageModel") ImageModel azureImageModel) {
        this.azureImageModel = azureImageModel;
    }

    /*
    @GetMapping("/generate")
    public Map<String, Object> generate(@RequestParam(defaultValue = "Un astronauta che cavalca un cavallo su Marte") String prompt) {
        logger.info("Richiesta generazione immagine ad Azure OpenAI...");

        // Usiamo le opzioni specifiche di Azure per forzare il deployment a runtime
        ImageResponse response = azureImageModel.call(
                new ImagePrompt(prompt,
                        AzureOpenAiImageOptions.builder()
                                .deploymentName("gpt-image-1-mini")
                                .model("gpt-image-1-mini")
                                .width(1024)
                                .height(1024)
                                .build())
        );

        return Map.of(
                "prompt", prompt,
                "image_url", response.getResult().getOutput().getUrl(),
                "metadata", response.getMetadata()
        );
    }
    */
}