package spring.ai.example.spring_ai_demo.web.ai.config;

import com.networknt.schema.OutputFormat;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.ai")
public class AiFeatureProperties {

    private String systemMessage;
    private Features features = new Features();

    @Data
    public static class Features {
        private boolean memoryEnabled;
        private int memoryMaxMessages = 10; // Default
        private boolean toolsEnabled;
        private boolean loggingEnabled;
        private boolean structuredOutput;
        private OutputFormat outputFormat = OutputFormat.TEXT; // Default
        private boolean debugTools = false;
    }

    // Enum per forzare i valori accettati nello YAML
    public enum OutputFormat {
        TEXT, JSON
    }
}