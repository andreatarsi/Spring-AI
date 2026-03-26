package backup.controller_backup;

import org.springframework.ai.image.ImageResponse;
import backup.service.ImageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/ai/image")
public class BaseImageController {

    private final ImageService imageService;

    public BaseImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping("/generate")
    public Map<String, Object> generate(@RequestParam(defaultValue = "Un gatto spaziale che mangia pizza") String prompt) {
        ImageResponse response = imageService.generaImmagine(prompt, 1024, 1024);

        // Restituiamo l'URL o il Base64 prodotto dal modello
        return Map.of(
                "prompt", prompt,
                "risultato", response.getResult().getOutput()
        );
    }
}