package spring.ai.example.spring_ai_demo.web.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import spring.ai.example.spring_ai_demo.service.AiAudioService;

@RestController
@RequestMapping("/api/v1/audio")
public class AudioController {

    private final AiAudioService aiAudioService;

    public AudioController(AiAudioService aiAudioService) {
        this.aiAudioService = aiAudioService;
    }

    // Usiamo POST perché stiamo inviando un file binario (Multipart)
    @PostMapping("/transcribe")
    public String transcribe(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String model) { // <-- AGGIUNTO IL TELECOMANDO

        if (file.isEmpty()) {
            return "Errore: File audio mancante.";
        }

        // Passiamo la risorsa binaria e la preferenza del modello al Service
        return aiAudioService.transcribeAudio(file.getResource(), model);
    }
}