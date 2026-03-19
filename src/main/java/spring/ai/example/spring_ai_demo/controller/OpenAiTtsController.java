package spring.ai.example.spring_ai_demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.audio.tts.TextToSpeechModel; // NUOVO PACKAGE
import org.springframework.ai.audio.tts.TextToSpeechPrompt; // NUOVO PACKAGE
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * Controller per OpenAI TTS migrato alle nuove interfacce standard.
 */
// @RestController
@RequestMapping("/ai/openai-tts")
public class OpenAiTtsController {

    private static final Logger logger = LoggerFactory.getLogger(OpenAiTtsController.class);

    private final TextToSpeechModel ttsModel;

    public OpenAiTtsController(@Qualifier("openAiAudioSpeechModel") TextToSpeechModel ttsModel) {
        this.ttsModel = ttsModel;
    }

    /*
    @GetMapping(value = "/speak", produces = "audio/mpeg")
    public byte[] speak(@RequestParam(defaultValue = "Sto usando le nuove interfacce standard di Spring AI!") String text) {
        logger.info("Sintesi vocale in corso...");

        OpenAiAudioSpeechOptions options = OpenAiAudioSpeechOptions.builder()
                .withModel("gpt-4o-mini-tts")
                .withVoice(OpenAiAudioApi.SpeechRequest.Voice.NOVA)
                .withSpeed(1.1) // Nota: Double, non Float!
                .build();

        return ttsModel.call(new TextToSpeechPrompt(text, options)).getResult().getOutput();
    }

    @GetMapping(value = "/stream", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Flux<byte[]> stream(@RequestParam String text) {
        return ttsModel.stream(text);
    }
    */
}