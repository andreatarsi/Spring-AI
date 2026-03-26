package backup.controller_backup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;


/**
 * Controller per ElevenLabs TTS.
 * Utilizzato per narrazioni ultra-realistiche e controllo vocale fine.
 */
// @RestController
@RequestMapping("/ai/elevenlabs-speech")
public class ElevenLabsTtsController {

    private static final Logger logger = LoggerFactory.getLogger(ElevenLabsTtsController.class);

    private final TextToSpeechModel elevenLabsModel;

    public ElevenLabsTtsController(@Qualifier("elevenLabsTextToSpeechModel") TextToSpeechModel elevenLabsModel) {
        this.elevenLabsModel = elevenLabsModel;
    }

    /*
    @GetMapping(value = "/narrate", produces = "audio/mpeg")
    public byte[] narrate(@RequestParam(defaultValue = "Questa è una voce generata con tecnologia ElevenLabs.") String text) {
        logger.info("Generazione audio ultra-realistico con ElevenLabs...");

        // Impostiamo parametri per una voce stabile ma espressiva
        var voiceSettings = new ElevenLabsApi.SpeechRequest.VoiceSettings(0.65f, 0.75f, 0.1f, true);

        ElevenLabsTextToSpeechOptions options = ElevenLabsTextToSpeechOptions.builder()
                .withModel("eleven_multilingual_v2")
                .withVoiceSettings(voiceSettings)
                .withOutputFormat(ElevenLabsApi.OutputFormat.MP3_44100_128.getValue())
                .build();

        return elevenLabsModel.call(new TextToSpeechPrompt(text, options)).getResult().getOutput();
    }
    */
}