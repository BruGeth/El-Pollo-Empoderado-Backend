package com.elpolloempoderado.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class DatabaseConnectionTest {

    @Test
    void contextLoads() {
        // Este test verifica que el contexto de Spring se carga correctamente
        // Si la conexión a la BD falla, este test fallará
    }
}