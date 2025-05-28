package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test") // Active le profil test pour ne pas exécuter le CommandLineRunner
class DemoApplicationTests {

    @Test
    void contextLoads() {
    }
}
