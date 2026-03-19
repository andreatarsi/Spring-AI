package spring.ai.example.spring_ai_demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.zhipuai.ZhiPuAiImageOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller per ZhiPuAI CogView.
 * Un'ottima alternativa a DALL-E per la generazione di immagini di alta qualità.
 */
// @RestController
@RequestMapping("/ai/zhipuai-image")
public class ZhiPuAiImageController {

    private static final Logger logger = LoggerFactory.getLogger(ZhiPuAiImageController.class);

    private final ImageModel zhipuImageModel;

    public ZhiPuAiImageController(@Qualifier("zhiPuAiImageModel") ImageModel zhipuImageModel) {
        this.zhipuImageModel = zhipuImageModel;
    }

    /*
    @GetMapping("/generate")
    public Map<String, Object> generate(@RequestParam(defaultValue = "Un dragone cinese in stile cyberpunk") String prompt) {
        logger.info("Generazione immagine con ZhiPuAI CogView...");

        ImageResponse response = zhipuImageModel.call(
                new ImagePrompt(prompt,
                        ZhiPuAiImageOptions.builder()
                                .withModel("cogview-3")
                                .withN(1)
                                .build())
        );

        return Map.of(
                "prompt", prompt,
                "url", response.getResult().getOutput().getUrl(),
                "provider", "ZhiPuAI CogView"
        );
    }
    */
}