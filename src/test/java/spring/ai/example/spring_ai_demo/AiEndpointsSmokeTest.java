package spring.ai.example.spring_ai_demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import spring.ai.example.spring_ai_demo.web.dto.ChatResponseDTO;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

// Avvia Tomcat su una porta casuale per evitare conflitti se l'app è già in esecuzione
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AiEndpointsSmokeTest {

    private static final Logger log = LoggerFactory.getLogger(AiEndpointsSmokeTest.class);

    // Spring inietta qui la porta casuale scelta per questo test
    @LocalServerPort
    private int port;

    private RestClient restClient;

    @BeforeEach
    void setUp() {
        // Costruiamo il client usando il builder statico nativo!
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void test1_QuickChat_ShouldReturn200AndTokens() {
        log.info("🧪 Esecuzione TEST 1: Quick Chat (Nessun Crash)");

        ResponseEntity<ChatResponseDTO> response = restClient.get()
                .uri("/api/v1/chat/quick?message=Dimmi ciao&chatId=test_1")
                .retrieve()
                .toEntity(ChatResponseDTO.class);

        // 1. Nessun crash (HTTP 200)
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        ChatResponseDTO body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.answer()).isNotNull();
        assertThat(body.answer().toString()).isNotBlank();

        // 2. Metadati presenti
        assertThat(body.modelUsed()).isNotBlank();
        assertThat(body.totalTokens()).isGreaterThan(0);

        log.info("✅ TEST 1 SUPERATO! Modello ha risposto con: {}", body.answer());
    }

    @Test
    void test2_StructuredOutput_ShouldParseJsonCorrectly() {
        log.info("🧪 Esecuzione TEST 2: Structured Output (Forzatura JSON)");

        ResponseEntity<ChatResponseDTO> response = restClient.get()
                .uri("/api/v1/chat/structured?message=Descrivi la luna in 3 parole&chatId=test_2")
                .retrieve()
                .toEntity(ChatResponseDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ChatResponseDTO body = response.getBody();
        assertThat(body).isNotNull();

        // Se il JSON ha funzionato, 'answer' è una Map parsata
        assertThat(body.answer()).isInstanceOf(Map.class);

        @SuppressWarnings("unchecked")
        Map<String, String> answerMap = (Map<String, String>) body.answer();

        assertThat(answerMap).containsKey("answer");
        assertThat(answerMap).containsKey("category");
        assertThat(answerMap).containsKey("mood");

        log.info("✅ TEST 2 SUPERATO! JSON generato e parsato correttamente: {}", answerMap);
    }

    @Test
    void test3_MemoryIsolation_ShouldRememberAndForget() {
        log.info("🧪 Esecuzione TEST 3: Isolamento Memoria Utenti");

        // Utente A si presenta (non ci interessa la risposta, usiamo toBodilessEntity)
        restClient.get()
                .uri("/api/v1/chat/quick?message=Mi chiamo Pippo&chatId=utente_A")
                .retrieve()
                .toBodilessEntity();

        // Utente A chiede chi è
        ResponseEntity<ChatResponseDTO> responseA = restClient.get()
                .uri("/api/v1/chat/quick?message=Come mi chiamo?&chatId=utente_A")
                .retrieve()
                .toEntity(ChatResponseDTO.class);

        assertThat(responseA.getBody().answer().toString()).containsIgnoringCase("Pippo");

        // Utente B chiede chi è (NON deve sapere di Pippo)
        ResponseEntity<ChatResponseDTO> responseB = restClient.get()
                .uri("/api/v1/chat/quick?message=Come mi chiamo?&chatId=utente_B")
                .retrieve()
                .toEntity(ChatResponseDTO.class);

        assertThat(responseB.getBody().answer().toString()).doesNotContainIgnoringCase("Pippo");

        log.info("✅ TEST 3 SUPERATO! La memoria è isolata correttamente per ogni utente.");
    }
}