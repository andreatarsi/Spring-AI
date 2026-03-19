package spring.ai.example.spring_ai_demo.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import spring.ai.example.spring_ai_demo.service.TranscriptionService;

import java.util.Map;

@RestController
@RequestMapping("/ai/audio")
public class BaseAudioController {

    private final TranscriptionService transcriptionService;

    public BaseAudioController(TranscriptionService transcriptionService) {
        this.transcriptionService = transcriptionService;
    }

    @GetMapping("/transcribe")
    public Map<String, String> transcribe(@RequestParam String pathFile) {
        // Esempio: pathFile = "C:/audio/test.mp3"
        FileSystemResource audioFile = new FileSystemResource(pathFile);

        String testoTrascritto = transcriptionService.trascriviAudioSemplice(audioFile);

        return Map.of(
                "file", pathFile,
                "trascrizione", testoTrascritto
        );
    }
}