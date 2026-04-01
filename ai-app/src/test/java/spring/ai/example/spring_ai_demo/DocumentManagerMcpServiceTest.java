package spring.ai.example.spring_ai_demo.service;

import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class DocumentManagerMcpServiceTest {

    @Autowired
    private DocumentManagerMcpService documentManagerService;

    @Test
    void testAutoCompletamentoDocumenti() {
        // Quando l'utente (o l'Inspector) inizia a digitare "doc-"
        List<String> suggerimenti = documentManagerService.completeDocumentId("doc-");

        // Verifichiamo che il server filtri correttamente e ci restituisca le chiavi esatte
        assertThat(suggerimenti).isNotEmpty();
        assertThat(suggerimenti).contains("doc-architettura", "doc-sicurezza", "doc-policy-ferie");
        assertThat(suggerimenti).doesNotContain("report-q1"); // Non inizia con "doc-"
    }

    @Test
    void testLetturaRisorsaEsistente() {
        // Simuliamo la richiesta della risorsa (Il click su Read Resource)
        McpSchema.ReadResourceResult result = documentManagerService.getDocument("doc-architettura");

        // Verifichiamo la risposta
        assertNotNull(result);
        assertThat(result.contents()).hasSize(1);

        McpSchema.TextResourceContents contenuto = (McpSchema.TextResourceContents) result.contents().get(0);
        assertThat(contenuto.text()).contains("Spring Boot 3");
    }

    @Test
    void testGenerazionePrompt() {
        // Simuliamo la richiesta del prompt
        McpSchema.GetPromptResult promptResult = documentManagerService.documentSummaryPrompt("report-q1");

        assertThat(promptResult.description()).isEqualTo("Riassunto Documento");
        assertThat(promptResult.messages().get(0).content().type()).isEqualTo("text");

        // Verifichiamo che il prompt abbia "iniettato" il testo corretto del report
        String promptText = ((McpSchema.TextContent) promptResult.messages().get(0).content()).text();
        assertThat(promptText).contains("Il primo trimestre ha visto un aumento del 20%");
    }
}