package spring.ai.example.spring_ai_demo.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import spring.ai.example.spring_ai_demo.service.NarrationService;

import java.util.Map;

@RestController
@RequestMapping("/ai/speech")
public class TtsController {

    private final NarrationService narrationService;

    public TtsController(NarrationService narrationService) {
        this.narrationService = narrationService;
    }

    // Endpoint per scaricare un MP3
    @GetMapping(value = "/synthesize", produces = "audio/mpeg")
    public ResponseEntity<byte[]> synthesize(@RequestParam(defaultValue = "Benvenuti nel futuro di Spring AI!") String text) {
        byte[] audio = narrationService.sintetizzaAudio(text);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"speech.mp3\"")
                .body(audio);
    }

    // Endpoint per lo streaming real-time
    @GetMapping(value = "/stream", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Flux<byte[]> stream(@RequestParam String text) {
        return narrationService.streamingAudio(text);
    }
}