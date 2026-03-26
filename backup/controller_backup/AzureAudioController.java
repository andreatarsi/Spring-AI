package backup.controller_backup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;


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