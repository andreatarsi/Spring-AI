package spring.ai.example.spring_ai_demo.config;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class WeatherToolsConfig {

    @Tool(description = "Get the current weather for a specific location")
    public String currentWeather(
            @ToolParam(description = "Il nome della città") String location) {

        System.out.println("--- ESECUZIONE TOOL METEO PER: " + location + " ---");
        return "Temperatura: 25.0°C, Previsione: Soleggiato a " + location;
    }
}