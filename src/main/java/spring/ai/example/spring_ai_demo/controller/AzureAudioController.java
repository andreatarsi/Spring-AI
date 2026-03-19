package spring.ai.example.spring_ai_demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.azure.openai.AzureOpenAiAudioTranscriptionOptions;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller per la trascrizione audio tramite Azure OpenAI Whisper.
 */
// @RestController
@RequestMapping("/ai/azure-audio")
public class AzureAudioController {

    private static final Logger logger = LoggerFactory.getLogger(AzureAudioController.class);

    private final TranscriptionModel azureTranscriptionModel;

    public AzureAudioController(@Qualifier("azureOpenAiTranscriptionModel") TranscriptionModel azureTranscriptionModel) {
        this.azureTranscriptionModel = azureTranscriptionModel;
    }

    /*
    @GetMapping("/transcript")
    public Map<String, Object> transcript(@RequestParam String filePath) {
        logger.info("Inizio trascrizione Azure per il file: {}", filePath);

        FileSystemResource audioFile = new FileSystemResource(filePath);

        // Configurazione runtime per ottenere un formato SRT (sottotitoli)
        AzureOpenAiAudioTranscriptionOptions options = AzureOpenAiAudioTranscriptionOptions.builder()
                .withResponseFormat(AzureOpenAiAudioTranscriptionOptions.TranscriptResponseFormat.SRT)
                .withLanguage("it")
                .withTemperature(0f) // Massima precisione, meno creatività
                .build();

        AudioTranscriptionResponse response = azureTranscriptionModel.call(
                new AudioTranscriptionPrompt(audioFile, options)
        );

        return Map.of(
                "file", filePath,
                "risultato", response.getResult().getOutput(),
                "metadata", response.getMetadata()
        );
    }
    */
}