package br.com.fsj.salesplatform;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Teste de contexto da aplicação (smoke test).
 * 
 * <p>Valida que o contexto Spring carrega corretamente e que
 * todos os beans necessários são criados sem erros.</p>
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@SpringBootTest
@ActiveProfiles("test")
class SalesPlatformApplicationTests {

    /**
     * Testa se o contexto da aplicação carrega corretamente.
     * 
     * <p>Este é um smoke test básico que garante que a configuração
     * do Spring Boot está correta e que a aplicação pode iniciar.</p>
     */
    @Test
    void contextLoads() {
        // Se o contexto carregar sem exceções, o teste passa
    }
}

