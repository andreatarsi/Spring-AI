package spring.ai.example.spring_ai_demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.moderation.Moderation;
import org.springframework.ai.moderation.ModerationModel;
import org.springframework.ai.moderation.ModerationPrompt;
import org.springframework.ai.moderation.ModerationResponse;
import org.springframework.ai.openai.OpenAiModerationOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller per la moderazione dei contenuti.
 * Verifica se un testo viola le policy di sicurezza.
 */
// @RestController
@RequestMapping("/ai/moderation")
public class ModerationController {

    private static final Logger logger = LoggerFactory.getLogger(ModerationController.class);

    private final ModerationModel moderationModel;

    public ModerationController(@Qualifier("openAiModerationModel") ModerationModel moderationModel) {
        this.moderationModel = moderationModel;
    }

    /*
    @GetMapping("/check")
    public Map<String, Object> checkContent(@RequestParam String text) {
        logger.info("Verifica sicurezza contenuto...");

        ModerationResponse response = moderationModel.call(
                new ModerationPrompt(text,
                    OpenAiModerationOptions.builder()
                        .model("omni-moderation-latest")
                        .build())
        );

        Moderation result = response.getResult().getOutput();

        // Prendiamo il primo risultato della lista (solitamente ce n'è solo uno)
        var firstResult = result.getResults().get(0);

        return Map.of(
                "text", text,
                "isFlagged", firstResult.isFlagged(),
                "categories", firstResult.getCategories(),
                "scores", firstResult.getCategoryScores()
        );
    }
    */
}