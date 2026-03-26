package backup.controller_backup;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.zhipuai.ZhiPuAiImageOptions; // Verifica questo dopo il reload
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller per ZhiPuAI CogView.
 */
@RestController
@RequestMapping("/ai/zhipuai-image")
public class ZhiPuAiImageController {

    private static final Logger logger = LoggerFactory.getLogger(ZhiPuAiImageController.class);

    private final ImageModel zhipuImageModel;

    // Il nome del bean registrato da Spring Boot è zhiPuAiImageModel
    public ZhiPuAiImageController(@Qualifier("zhiPuAiImageModel") ImageModel zhipuImageModel) {
        this.zhipuImageModel = zhipuImageModel;
    }

    @GetMapping("/generate")
    public Map<String, Object> generate(@RequestParam(defaultValue = "Un dragone cinese in stile cyberpunk") String prompt) {
        logger.info("Generazione immagine con ZhiPuAI CogView...");

        ImageResponse response = zhipuImageModel.call(
                new ImagePrompt(prompt,
                        ZhiPuAiImageOptions.builder()
                                .model("cogview-3") // Nella 2.0.0-M2 il metodo è spesso .model() invece di .withModel()
                                .build())
        );

        return Map.of(
                "prompt", prompt,
                "url", response.getResult().getOutput().getUrl(),
                "provider", "ZhiPuAI CogView"
        );
    }
}