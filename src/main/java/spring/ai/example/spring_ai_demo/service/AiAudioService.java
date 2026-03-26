package spring.ai.example.spring_ai_demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.audio.transcription.TranscriptionModel; // <-- L'import corretto per la v2.0.0!
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class AiAudioService {

    private static final Logger log = LoggerFactory.getLogger(AiAudioService.class);

    // Usiamo il nuovo nome dell'interfaccia
    private final ObjectProvider<TranscriptionModel> transcriptionModelProvider;

    public AiAudioService(ObjectProvider<TranscriptionModel> transcriptionModelProvider) {
        this.transcriptionModelProvider = transcriptionModelProvider;
    }

    public String transcribeAudio(Resource audioFile) {
        TranscriptionModel transcriptionModel = transcriptionModelProvider.getIfAvailable();

        if (transcriptionModel == null) {
            throw new IllegalStateException("Nessun motore di trascrizione audio configurato o attivo in questo profilo (Prova con OpenAI)!");
        }

        log.info("Richiesta trascrizione per il file: '{}'", audioFile.getFilename());

        AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(audioFile);
        AudioTranscriptionResponse response = transcriptionModel.call(prompt);

        return response.getResult().getOutput();
    }
}