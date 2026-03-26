package backup.service; // <-- PACCHETTO CORRETTO!

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class ServizioOrdini {

    private static final Logger logger = LoggerFactory.getLogger(ServizioOrdini.class);

    // I nostri record di input e output
    public record OrdineRequest(
            @JsonProperty(required = true, value = "idOrdine")
            @JsonPropertyDescription("L'identificativo numerico dell'ordine, ad esempio '12345'")
            String idOrdine) {}

    public record OrdineResponse(String stato, String dataConsegna, String corriere) {}

    // Il nostro Tool
    @Tool(description = "Recupera lo stato della spedizione e i dettagli di un ordine e-commerce partendo dal suo ID numerico.")
    public OrdineResponse controllaStatoOrdine(OrdineRequest request) {

        // Questo log ora stamperà il numero corretto invece di "null"!
        logger.info("⚙️ [TOOL ESEGUITO MAGICAMENTE DA OLLAMA] Cerco nel database l'ordine ID: {}", request.idOrdine());

        // Simuliamo il Database
        if ("12345".equals(request.idOrdine())) {
            return new OrdineResponse("In Transito verso Milano", "Domani mattina", "Corriere Veloce SPA");
        } else if ("999".equals(request.idOrdine())) {
            return new OrdineResponse("Consegnato", "Ieri", "Poste");
        } else {
            return new OrdineResponse("Ordine inesistente", "N/A", "N/A");
        }
    }
}