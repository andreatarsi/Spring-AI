package spring.ai.example.spring_ai_demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
// import org.springframework.ai.zhipuai.ZhiPuAiEmbeddingOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Controller per ZhiPuAI (BigModel).
 * L'ultimo tassello della nostra collezione mondiale.
 */
// @RestController
@RequestMapping("/ai/zhipuai")
public class ZhiPuAiEmbeddingController {

    private static final Logger logger = LoggerFactory.getLogger(ZhiPuAiEmbeddingController.class);

    private final EmbeddingModel zhipuModel;

    public ZhiPuAiEmbeddingController(@Qualifier("zhiPuAiEmbeddingModel") EmbeddingModel zhipuModel) {
        this.zhipuModel = zhipuModel;
    }

    /*
    @GetMapping("/embed")
    public Map<String, Object> testZhipu(@RequestParam(defaultValue = "L'ultima frontiera dell'embedding") String testo) {
        logger.info("Calcolo vettore con ZhiPuAI (BigModel)...");

        EmbeddingResponse response = zhipuModel.call(
                new EmbeddingRequest(List.of(testo),
                        ZhiPuAiEmbeddingOptions.builder()
                                .model("embedding-3")
                                .dimensions(1024)
                                .build()
                )
        );

        return Map.of(
                "testo", testo,
                "provider", "ZhiPuAI",
                "dimensioni", response.getResult().getOutput().length
        );
    }
    */
}