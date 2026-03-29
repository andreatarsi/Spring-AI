package spring.ai.example.spring_ai_demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AiAudioService {

    private static final Logger log = LoggerFactory.getLogger(AiAudioService.class);

    private final Map<String, TranscriptionModel> transcriptionModels;

    public AiAudioService(Map<String, TranscriptionModel> transcriptionModels) {
        this.transcriptionModels = transcriptionModels;
    }

    // Aggiunto parametro model
    public String transcribeAudio(Resource audioFile, String requestedModel) {

        String engineKey = (requestedModel != null && !requestedModel.isBlank())
                ? requestedModel.toLowerCase()
                : "openai";

        // Whisper di OpenAI è il re indiscusso delle trascrizioni per ora
        String activeEngine = switch (engineKey) {
            case "openai", "whisper" -> "OpenAiTranscriptionModel";
            default -> "OpenAiTranscriptionModel";
        };

        TranscriptionModel activeModel = transcriptionModels.get(activeEngine);

        if (activeModel == null) {
            throw new IllegalStateException("Motore Audio non trovato o disabilitato: " + activeEngine + ". Modelli disponibili: " + transcriptionModels.keySet());
        }

        log.info("🎙️ Richiesta trascrizione su [{}] per il file: '{}'", activeEngine, audioFile.getFilename());

        AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(audioFile);
        AudioTranscriptionResponse response = activeModel.call(prompt);

        return response.getResult().getOutput();
    }
}