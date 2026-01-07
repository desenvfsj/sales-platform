package br.com.fsj.salesplatform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

/**
 * Configuração de CORS (Cross-Origin Resource Sharing).
 * 
 * <p>Permite que a API seja acessada de diferentes origens (front-end, apps, etc).</p>
 * 
 * <p><strong>⚠️ ATENÇÃO:</strong> Esta configuração é permissiva para desenvolvimento.
 * Em produção, restrinja as origens permitidas.</p>
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@Configuration
public class CorsConfig {

    /**
     * Configura filtro CORS global.
     * 
     * <p>Configurações aplicadas:</p>
     * <ul>
     *   <li>Origens permitidas: todas (*) - AJUSTAR EM PRODUÇÃO</li>
     *   <li>Métodos: GET, POST, PUT, PATCH, DELETE, OPTIONS</li>
     *   <li>Headers: todos (*)</li>
     *   <li>Credenciais: permitidas</li>
     *   <li>Max Age: 3600 segundos (1 hora)</li>
     * </ul>
     * 
     * @return filtro CORS configurado
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // ⚠️ DESENVOLVIMENTO: Permitir todas as origens
        // 🔒 PRODUÇÃO: Substituir por lista específica de domínios
        config.setAllowedOriginPatterns(List.of("*"));
        
        // Métodos HTTP permitidos
        config.setAllowedMethods(Arrays.asList(
            "GET", 
            "POST", 
            "PUT", 
            "PATCH", 
            "DELETE", 
            "OPTIONS"
        ));
        
        // Headers permitidos (todos)
        config.setAllowedHeaders(List.of("*"));
        
        // Headers expostos na resposta (importante para SSE)
        config.setExposedHeaders(Arrays.asList(
            "Content-Type",
            "Cache-Control",
            "Content-Length",
            "X-Requested-With"
        ));
        
        // Permitir envio de credenciais (cookies, auth headers)
        config.setAllowCredentials(true);
        
        // Tempo de cache da configuração CORS (segundos)
        config.setMaxAge(3600L);
        
        // Aplicar configuração a todos os endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}

