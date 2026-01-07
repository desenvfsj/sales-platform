package br.com.fsj.salesplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principal da aplicação Sales Platform.
 * 
 * <p>Este microsserviço é responsável por:</p>
 * <ul>
 *   <li>Gestão de carrinho de compras (criação, atualização, remoção de itens)</li>
 *   <li>Finalização de vendas (checkout)</li>
 *   <li>Registro de pedidos/vendas</li>
 *   <li>Exposição de APIs REST para múltiplos canais (web, app, PDV, parceiros)</li>
 * </ul>
 * 
 * <p><strong>Stack Tecnológica:</strong></p>
 * <ul>
 *   <li>Java 25</li>
 *   <li>Spring Boot 4.0.1</li>
 *   <li>PostgreSQL (banco de dados)</li>
 *   <li>Flyway (versionamento de banco)</li>
 *   <li>MapStruct (conversão DTO ↔ Entity)</li>
 *   <li>Springdoc OpenAPI (documentação)</li>
 * </ul>
 * 
 * <p><strong>Arquitetura:</strong></p>
 * <ul>
 *   <li>Controller → Service → Repository</li>
 *   <li>DTOs para entrada/saída da API</li>
 *   <li>Tratamento de exceções centralizado (RFC 9457)</li>
 *   <li>Versionamento de API via URL (/api/v1/...)</li>
 * </ul>
 * 
 * <p><strong>Endpoints de Documentação:</strong></p>
 * <ul>
 *   <li>Swagger UI: http://localhost:8080/swagger-ui.html</li>
 *   <li>OpenAPI JSON: http://localhost:8080/v3/api-docs</li>
 *   <li>Health Check: http://localhost:8080/actuator/health</li>
 * </ul>
 * 
 * @author Sales Platform Team
 * @version 1.0.0
 * @since 1.0.0
 */
@SpringBootApplication
public class SalesPlatformApplication {

    /**
     * Método principal que inicia a aplicação Spring Boot.
     * 
     * @param args argumentos de linha de comando
     */
    public static void main(String[] args) {
        SpringApplication.run(SalesPlatformApplication.class, args);
    }
}

