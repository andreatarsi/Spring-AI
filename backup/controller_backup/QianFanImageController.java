package backup.controller_backup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.image.ImageModel;
// import org.springframework.ai.qianfan.QianFanImageOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;


/**
 * Controller per Baidu QianFan Image Generation.
 * Utilizza i modelli della famiglia CogView tramite la piattaforma Baidu.
 */
// @RestController
@RequestMapping("/ai/qianfan-image")
public class QianFanImageController {

    private static final Logger logger = LoggerFactory.getLogger(QianFanImageController.class);

    private final ImageModel qianfanImageModel;

    public QianFanImageController(@Qualifier("qianfanImageModel") ImageModel qianfanImageModel) {
        this.qianfanImageModel = qianfanImageModel;
    }

    /*
    @GetMapping("/generate")
    public Map<String, Object> generate(@RequestParam(defaultValue = "Un giardino zen in stile futuristico") String prompt) {
        logger.info("Generazione immagine con Baidu QianFan...");

        ImageResponse response = qianfanImageModel.call(
                new ImagePrompt(prompt,
                        QianFanImageOptions.builder()
                                .withModel("CogView")
                                .withWidth(1024)
                                .withHeight(1024)
                                .build())
        );

        return Map.of(
                "prompt", prompt,
                "url", response.getResult().getOutput().getUrl(),
                "provider", "Baidu QianFan"
        );
    }
    */
}