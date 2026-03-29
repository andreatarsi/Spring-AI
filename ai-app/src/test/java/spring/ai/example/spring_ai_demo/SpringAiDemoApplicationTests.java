package spring.ai.example.spring_ai_demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringAiDemoApplicationTests {

    @Test
    void contextLoads() {
        // Se arriva fin qui senza esplodere, vuol dire che:
        // 1. I Bean (Controller, Service) sono iniettati correttamente.
        // 2. Le dipendenze Maven sono risolte.
        // 3. I file di properties sono formattati bene.
    }

}