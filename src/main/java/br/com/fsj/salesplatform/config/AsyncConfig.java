package br.com.fsj.salesplatform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuração para processamento assíncrono.
 * 
 * <p>Configura thread pool para operações assíncronas, incluindo envio de eventos SSE.</p>
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Configura executor para tarefas assíncronas.
     * 
     * <p>Configurações otimizadas para envio de eventos SSE:</p>
     * <ul>
     *   <li>Core pool: 5 threads</li>
     *   <li>Max pool: 20 threads</li>
     *   <li>Queue capacity: 100 tarefas</li>
     * </ul>
     * 
     * @return executor configurado
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // Número de threads core (sempre ativas)
        executor.setCorePoolSize(5);
        
        // Número máximo de threads
        executor.setMaxPoolSize(20);
        
        // Capacidade da fila de espera
        executor.setQueueCapacity(100);
        
        // Prefixo do nome das threads (facilita debug)
        executor.setThreadNamePrefix("sse-async-");
        
        // Aguardar conclusão de tarefas no shutdown
        executor.setWaitForTasksToCompleteOnShutdown(true);
        
        // Timeout para aguardar conclusão (segundos)
        executor.setAwaitTerminationSeconds(30);
        
        executor.initialize();
        return executor;
    }
}

