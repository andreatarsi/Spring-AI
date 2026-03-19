package spring.ai.example.spring_ai_demo.service;

// 1. OpenAI: I modelli TTS e le opzioni sono stati spostati qui
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;

// 2. Interfaccia Trascrizione (Universale)
import org.springframework.ai.audio.transcription.TranscriptionModel;

// 3. Interfaccia TTS (Universale)
import org.springframework.ai.audio.tts.TextToSpeechModel;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

//@Service
public class NarrationService {

    private final TextToSpeechModel ttsModel;

    public NarrationService(TextToSpeechModel ttsModel) {
        this.ttsModel = ttsModel;
    }

    // Generazione "tutto in una volta"
    public byte[] sintetizzaAudio(String testo) {
        return ttsModel.call(testo);
    }

    // Generazione in streaming (per risposte lunghe)
    public Flux<byte[]> streamingAudio(String testo) {
        return ttsModel.stream(testo);
    }
}