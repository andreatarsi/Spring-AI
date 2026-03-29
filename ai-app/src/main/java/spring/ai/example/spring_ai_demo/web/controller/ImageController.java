package spring.ai.example.spring_ai_demo.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import spring.ai.example.spring_ai_demo.service.AiImageService;

@RestController
@RequestMapping("/api/v1/image")
public class ImageController {

    private final AiImageService aiImageService;

    public ImageController(AiImageService aiImageService) {
        this.aiImageService = aiImageService;
    }

    @GetMapping("/generate")
    public String generateImage(
            @RequestParam String prompt,
            @RequestParam(required = false) String model) { // <-- AGGIUNTO IL TELECOMANDO

        // Passiamo il modello al Service, che pescherà il bean giusto dalla Mappa
        return aiImageService.generateImage(prompt, model);
    }
}