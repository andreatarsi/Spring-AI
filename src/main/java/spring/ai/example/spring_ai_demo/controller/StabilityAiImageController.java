package spring.ai.example.spring_ai_demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;

// NUOVO: Rimosso .api e corretta la gerarchia
// Togli il vecchio e prova questo:
import org.springframework.ai.stabilityai.api.StabilityAiImageOptions;
// Se usi le classi API dirette, anch'esse sono state spostate:
// import org.springframework.ai.stabilityai.api.StabilityAiApi; -> org.springframework.ai.stabilityai.api.StabilityAiApi (dipende dalla versione, ma solitamente resta sotto .api solo la classe di basso livello)

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller per Stability AI.
 * Ideale per chi vuole controllo totale su sampler, steps e stili artistici.
 */
// @RestController
@RequestMapping("/ai/stability-image")
public class StabilityAiImageController {

    private static final Logger logger = LoggerFactory.getLogger(StabilityAiImageController.class);

    private final ImageModel stabilityImageModel;

    public StabilityAiImageController(@Qualifier("stabilityAiImageModel") ImageModel stabilityImageModel) {
        this.stabilityImageModel = stabilityImageModel;
    }

    /*
    @GetMapping("/generate")
    public Map<String, Object> generate(@RequestParam(defaultValue = "Un paesaggio cyberpunk sotto la pioggia") String prompt) {
        logger.info("Generazione immagine con Stability AI (Stile Cinematic)...");

        ImageResponse response = stabilityImageModel.call(
                new ImagePrompt(prompt,
                        StabilityAiImageOptions.builder()
                                .withStylePreset("cinematic") // Forza lo stile cinema
                                .withCfgScale(12.0f)          // Più aderenza al testo
                                .withSteps(30)                // Numero di passaggi di rifinitura
                                .withN(1)
                                .withWidth(1024)
                                .withHeight(1024)
                                .build())
        );

        // Nota: Stability AI restituisce spesso i dati in Base64 se configurato,
        // o un URL se passa attraverso il loro proxy API.
        return Map.of(
                "prompt", prompt,
                "risultato", response.getResult().getOutput(),
                "info", "Generato con Stable Diffusion"
        );
    }
    */
}