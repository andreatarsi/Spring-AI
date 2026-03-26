package backup.controller_backup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;


/**
 * Controller per la trascrizione audio tramite OpenAI Whisper-1.
 * Supporta formati testo, SRT e timestamp granulari.
 */
// @RestController
@RequestMapping("/ai/openai-audio")
public class OpenAiAudioController {

    private static final Logger logger = LoggerFactory.getLogger(OpenAiAudioController.class);

    private final TranscriptionModel openAiTranscriptionModel;

    public OpenAiAudioController(@Qualifier("openAiAudioTranscriptionModel") TranscriptionModel openAiTranscriptionModel) {
        this.openAiTranscriptionModel = openAiTranscriptionModel;
    }

    /*
    @GetMapping("/transcript")
    public Map<String, Object> transcript(@RequestParam String filePath) {
        logger.info("Richiesta trascrizione OpenAI per: {}", filePath);

        FileSystemResource audioFile = new FileSystemResource(filePath);

        // Esempio di opzioni avanzate: vogliamo i metadati dettagliati (verbose_json)
        OpenAiAudioTranscriptionOptions options = OpenAiAudioTranscriptionOptions.builder()
                .withResponseFormat(OpenAiAudioTranscriptionOptions.TranscriptResponseFormat.JSON)
                .withLanguage("it")
                .withTemperature(0f)
                .build();

        AudioTranscriptionResponse response = openAiTranscriptionModel.call(
                new AudioTranscriptionPrompt(audioFile, options)
        );

        return Map.of(
                "file", filePath,
                "testo", response.getResult().getOutput(),
                "metadata", response.getMetadata()
        );
    }
    */
}