package spring.ai.example.spring_ai_demo.service;

import org.springframework.ai.openai.audio.speech.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.audio.speech.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.transcription.TranscriptionModel;
import org.springframework.ai.tts.TextToSpeechModel;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
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