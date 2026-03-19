package spring.ai.example.spring_ai_demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
// import org.springframework.ai.qianfan.QianFanEmbeddingOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Controller per Baidu QianFan (modelli ERNIE).
 */
// @RestController
@RequestMapping("/ai/qianfan")
public class QianFanEmbeddingController {

    private static final Logger logger = LoggerFactory.getLogger(QianFanEmbeddingController.class);

    private final EmbeddingModel qianfanModel;

    public QianFanEmbeddingController(@Qualifier("qianfanEmbeddingModel") EmbeddingModel qianfanModel) {
        this.qianfanModel = qianfanModel;
    }

    /*
    @GetMapping("/embed")
    public Map<String, Object> testQianfan(@RequestParam(defaultValue = "你好世界 (Hello World)") String testo) {
        logger.info("Calcolo vettore con Baidu QianFan...");

        EmbeddingResponse response = qianfanModel.call(
                new EmbeddingRequest(List.of(testo),
                        QianFanEmbeddingOptions.builder()
                                .model("Embedding-V1")
                                .build()
                )
        );

        return Map.of(
                "testo", testo,
                "provider", "Baidu QianFan",
                "dimensioni", response.getResult().getOutput().length
        );
    }
    */
}