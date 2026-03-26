package backup.controller_backup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.mistralai.moderation.MistralAiModerationOptions;
import org.springframework.ai.moderation.ModerationModel;
import org.springframework.ai.moderation.ModerationPrompt;
import org.springframework.ai.moderation.ModerationResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller per la moderazione tramite Mistral AI.
 * Include controlli su PII (Privacy) e Law/Financial.
 */
 @RestController
@RequestMapping("/ai/mistral-moderation")
public class MistralModerationController {

    private static final Logger logger = LoggerFactory.getLogger(MistralModerationController.class);

    private final ModerationModel mistralModerationModel;

    public MistralModerationController(@Qualifier("mistralAiModerationModel") ModerationModel mistralModerationModel) {
        this.mistralModerationModel = mistralModerationModel;
    }


    @GetMapping("/check")
    public Map<String, Object> check(@RequestParam String text) {
        logger.info("Verifica sicurezza contenuto con Mistral AI...");

        ModerationResponse response = mistralModerationModel.call(
                new ModerationPrompt(text,
                        MistralAiModerationOptions.builder()
                                .model("mistral-moderation-latest")
                                .build())
        );

        var result = response.getResult().getOutput().getResults().get(0);

        return Map.of(
                "flagged", result.isFlagged(),
                "categories", Map.of(
                        "pii_detected", result.getCategories().isPii(),
                        "financial_advice", result.getCategories().isFinancial(),
                        "illegal_acts", result.getCategories().isLaw(),
                        "violence", result.getCategories().isViolence()
                ),
                "scores", result.getCategoryScores()
        );
    }

}