package spring.ai.example.spring_ai_demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.ai.mcp.spring.annotations.McpTool; // <-- Auto-importa questa classe!
import org.springaicommunity.mcp.annotation.McpMeta;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.mcp.McpToolUtils;
import io.modelcontextprotocol.spec.McpSchema;

@Service
public class WeatherMcpService {

    private static final Logger log = LoggerFactory.getLogger(WeatherMcpService.class);

    // 🌟 DUAL REGISTRATION: @Tool per il nostro Gateway locale, @McpTool per esporlo al mondo!
    @Tool(name = "get_local_weather", description = "Restituisce le condizioni meteo attuali per una data città. Usa questo tool quando l'utente chiede che tempo fa.")
    @McpTool(name = "get_local_weather", description = "Restituisce le condizioni meteo attuali per una data città. Usa questo tool quando l'utente chiede che tempo fa.")
    public String getWeather(String city) {
        log.info("🌤️ [MCP SERVER INTERNO] Il tool meteo è stato invocato per la città: {}", city);

        // Simuliamo la logica di business (qui potresti fare una chiamata a una vera API meteo!)
        if (city == null || city.isBlank()) {
            return "Errore: devi specificare una città.";
        }

        String cittaPulita = city.trim().toLowerCase();

        return switch (cittaPulita) {
            case "roma" -> "A Roma c'è il sole e ci sono 25 gradi.";
            case "milano" -> "A Milano è nuvoloso e ci sono 18 gradi.";
            case "buenos aires" -> "A Buenos Aires ci sono 30 gradi ed è sereno.";
            default -> "Per " + city + " ci sono 20 gradi e clima variabile.";
        };
    }

    @Tool(description = "Analizza il clima storico in modo approfondito")
    @org.springaicommunity.mcp.annotation.McpTool(
            name = "historicalWeatherAnalysis",
            description = "Analizza il clima storico in modo approfondito"
    )
    public String historicalWeatherAnalysis(
            String cityName,
            // Magia: Chiediamo direttamente l'Exchange!
            // Lo scanner lo nasconderà dall'Inspector e lo inietterà a runtime.
            io.modelcontextprotocol.server.McpSyncServerExchange exchange) {

        if (exchange != null) {
            // Inviamo un log al client
            exchange.loggingNotification(new io.modelcontextprotocol.spec.McpSchema.LoggingMessageNotification(
                    io.modelcontextprotocol.spec.McpSchema.LoggingLevel.INFO,
                    "HistoricalWeather",
                    "Inizio analisi storica per " + cityName
            ));

            // Inviamo il progresso
            exchange.progressNotification(new io.modelcontextprotocol.spec.McpSchema.ProgressNotification(
                    "storico-token",
                    0.5,
                    1.0,
                    "Consultazione archivi 1990-2020..."
            ));
        }

        // Simuliamo un calcolo lungo
        try { Thread.sleep(2000); } catch (InterruptedException e) {}

        return "L'analisi storica per " + cityName + " rivela un aumento medio delle temperature di 1.2 gradi negli ultimi 30 anni.";
    }

    @org.springaicommunity.mcp.annotation.McpTool(
            name = "dynamic_weather_analyzer",
            description = "Analizza il meteo leggendo i metadati"
    )
    public String dynamicWeatherAnalyzer(
            @org.springframework.ai.tool.annotation.ToolParam(description = "Città principale", required = true) String city,
            // Magia: catturiamo i metadati che hai inserito nella UI!
            McpMeta meta) {

        // Estraiamo i valori dal blocco _meta
        String giorni = (String) meta.get("giorni");
        String unita = (String) meta.get("unita_misura");

        if (giorni != null && unita != null) {
            return "Hai chiesto il meteo per " + city + ". Metadati ricevuti: " + giorni + " giorni in " + unita + "!";
        } else {
            return "Hai chiesto il meteo per " + city + " in modo standard, senza metadati.";
        }
    }
}