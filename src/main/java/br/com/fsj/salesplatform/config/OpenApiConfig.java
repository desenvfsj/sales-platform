package br.com.fsj.salesplatform.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuração do Springdoc OpenAPI para documentação automática da API.
 * 
 * <p>Esta configuração gera automaticamente a documentação Swagger UI
 * acessível em /swagger-ui.html e o JSON OpenAPI em /v3/api-docs.</p>
 * 
 * <p><strong>Endpoints de Documentação:</strong></p>
 * <ul>
 *   <li>Swagger UI: http://localhost:8080/swagger-ui.html</li>
 *   <li>OpenAPI JSON: http://localhost:8080/v3/api-docs</li>
 * </ul>
 * 
 * @see io.swagger.v3.oas.models.OpenAPI
 * @author Sales Platform Team
 * @since 1.0.0
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configura a documentação OpenAPI da aplicação.
     * 
     * @return instância configurada do OpenAPI
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(apiServers())
                .components(securityComponents())
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }

    /**
     * Define as informações básicas da API.
     */
    private Info apiInfo() {
        return new Info()
                .title("Sales Platform API")
                .description("""
                        API REST do microsserviço de Plataforma de Vendas.
                        
                        Responsável por:
                        - Gestão de carrinho de compras
                        - Finalização de vendas (checkout)
                        - Registro de pedidos
                        - Exposição de APIs para múltiplos canais (web, app, PDV, parceiros)
                        
                        **Padrões Implementados:**
                        - RFC 9457 (Problem Details) para tratamento de erros
                        - Versionamento de API via URL (/api/v1/...)
                        - Paginação para listagens
                        """)
                .version("v1.0.0")
                .contact(new Contact()
                        .name("Sales Platform Team")
                        .email("backend@fsj.com.br"));
    }

    /**
     * Define os servidores disponíveis para a API.
     */
    private List<Server> apiServers() {
        return List.of(
                new Server()
                        .url("http://localhost:8080")
                        .description("Ambiente de Desenvolvimento (Local)"),
                new Server()
                        .url("https://api.fsj.com.br")
                        .description("Ambiente de Produção")
        );
    }

    /**
     * Configura o esquema de segurança JWT (preparado para implementação futura).
     */
    private Components securityComponents() {
        return new Components()
                .addSecuritySchemes("bearer-jwt",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Token JWT para autenticação. Formato: Bearer {token}"));
    }
}

