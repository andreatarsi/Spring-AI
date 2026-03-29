package spring.ai.example.spring_ai_demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import spring.ai.example.spring_ai_demo.web.dto.ChatRequestDTO;
import spring.ai.example.spring_ai_demo.web.dto.ChatResponseDTO;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

// Avvia Tomcat su una porta casuale e imposta username/password fissi solo per il test!
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.security.user.name=testuser",
                "spring.security.user.password=testpass"
        }
)
class AiEndpointsSmokeTest {

    private static final Logger log = LoggerFactory.getLogger(AiEndpointsSmokeTest.class);

    @LocalServerPort
    private int port;

    private RestClient restClient;

    @BeforeEach
    void setUp() {
        // Costruiamo il client iniettando l'autenticazione Basic per superare Spring Security
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultHeaders(headers -> headers.setBasicAuth("testuser", "testpass"))
                .build();
    }

    @Test
    void test0_OmniPayloadPost_ShouldReturn200() {
        log.info("🧪 Esecuzione TEST 0: Omni-Payload POST (Il Gateway Universale)");

        // Creiamo il nostro JSON gigante in formato Oggetto Java
        // (message, conversationId, imageUrl, systemPrompt, webSearch, deepThinking, model)
        ChatRequestDTO payload = new ChatRequestDTO(
                "Dimmi ciao in modo formale",
                "sessione_post_1",
                null,
                "Sei un maggiordomo inglese.",
                false,
                false,
                "gemini" // Scegliamo esplicitamente il modello!
        );

        ResponseEntity<ChatResponseDTO> response = restClient.post()
                .uri("/api/v1/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .toEntity(ChatResponseDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ChatResponseDTO body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.modelUsed()).containsIgnoringCase("gemini");

        log.info("✅ TEST 0 SUPERATO! Risposta dal POST: {}", body.answer());
    }

    @Test
    void test1_QuickChat_ShouldReturn200AndTokens() {
        log.info("🧪 Esecuzione TEST 1: Quick Chat GET (Nessun Crash)");

        ResponseEntity<ChatResponseDTO> response = restClient.get()
                .uri("/api/v1/chat/quick?message=Dimmi ciao&chatId=test_1&model=gemini")
                .retrieve()
                .toEntity(ChatResponseDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        ChatResponseDTO body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.answer()).isNotNull();
        assertThat(body.answer().toString()).isNotBlank();
        assertThat(body.modelUsed()).isNotBlank();
        assertThat(body.totalTokens()).isGreaterThan(0);

        log.info("✅ TEST 1 SUPERATO! Modello ha risposto con: {}", body.answer());
    }

    @Test
    void test2_StructuredOutput_ShouldParseJsonCorrectly() {
        log.info("🧪 Esecuzione TEST 2: Structured Output (Forzatura JSON)");

        ResponseEntity<ChatResponseDTO> response = restClient.get()
                .uri("/api/v1/chat/structured?message=Descrivi la luna in 3 parole&chatId=test_2&model=gemini")
                .retrieve()
                .toEntity(ChatResponseDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ChatResponseDTO body = response.getBody();
        assertThat(body).isNotNull();

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

        restClient.get()
                .uri("/api/v1/chat/quick?message=Mi chiamo Pippo&chatId=utente_A&model=gemini")
                .retrieve()
                .toBodilessEntity();

        ResponseEntity<ChatResponseDTO> responseA = restClient.get()
                .uri("/api/v1/chat/quick?message=Come mi chiamo?&chatId=utente_A&model=gemini")
                .retrieve()
                .toEntity(ChatResponseDTO.class);

        assertThat(responseA.getBody().answer().toString()).containsIgnoringCase("Pippo");

        ResponseEntity<ChatResponseDTO> responseB = restClient.get()
                .uri("/api/v1/chat/quick?message=Come mi chiamo?&chatId=utente_B&model=gemini")
                .retrieve()
                .toEntity(ChatResponseDTO.class);

        assertThat(responseB.getBody().answer().toString()).doesNotContainIgnoringCase("Pippo");

        log.info("✅ TEST 3 SUPERATO! La memoria è isolata correttamente per ogni utente.");
    }
}