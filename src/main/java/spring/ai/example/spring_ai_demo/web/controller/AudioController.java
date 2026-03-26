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
    public String transcribe(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "Errore: File audio mancante.";
        }
        // Spring converte automaticamente il MultipartFile in una Resource compatibile
        return aiAudioService.transcribeAudio(file.getResource());
    }
}