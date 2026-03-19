package spring.ai.example.spring_ai_demo.service;


import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class TranscriptionService {

    private final TranscriptionModel transcriptionModel;

    public TranscriptionService(@Qualifier("openAiAudioTranscriptionModel") TranscriptionModel transcriptionModel) {
        this.transcriptionModel = transcriptionModel;
    }

    // Metodo super semplice: audio -> testo
    public String trascriviAudioSemplice(Resource audioFile) {
        return transcriptionModel.transcribe(audioFile);
    }

    // Metodo completo per avere anche i metadati
    public AudioTranscriptionResponse trascriviConDettagli(Resource audioFile) {
        AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(audioFile);
        return transcriptionModel.call(prompt);
    }
}