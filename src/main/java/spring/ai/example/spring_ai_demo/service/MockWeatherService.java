package spring.ai.example.spring_ai_demo.service;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

// Questo è un normale oggetto Java, ma le annotazioni lo trasformano
// in un "attrezzo" che l'Intelligenza Artificiale può decidere di usare!
public class MockWeatherService {

    @Tool(description = "Ottiene il meteo in tempo reale per una specifica città")
    public String weatherByLocation(
            @ToolParam(description = "Il nome della città o dello stato, es. Roma, Milano, Tokyo") String location) {

        // Nel mondo reale, qui faresti una chiamata HTTP a OpenWeatherMap.
        // Per ora, simuliamo la risposta.
        System.out.println("⚙️ [Tool Calling] L'IA ha invocato il metodo weatherByLocation per: " + location);
        return "A " + location + " la temperatura è di 22°C e c'è un bel sole.";
    }
}