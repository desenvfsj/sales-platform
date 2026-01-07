package br.com.fsj.salesplatform.adapters.out;

import br.com.fsj.salesplatform.adapters.in.controller.response.CartResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Serviço para gerenciar conexões Server-Sent Events (SSE) dos carrinhos.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@Service
public class SseEmitterService {
    
    private static final Logger log = LoggerFactory.getLogger(SseEmitterService.class);
    private static final Long TIMEOUT = 30 * 60 * 1000L; // 30 minutos
    
    private final Map<UUID, SseEmitter> emitters = new ConcurrentHashMap<>();
    
    /**
     * Cria um novo emitter para um carrinho.
     */
    public SseEmitter createEmitter(UUID cartId) {
        log.info("Criando SSE emitter para carrinho {}", cartId);
        
        SseEmitter emitter = new SseEmitter(TIMEOUT);
        
        emitter.onCompletion(() -> {
            log.info("SSE emitter completado para carrinho {}", cartId);
            emitters.remove(cartId);
        });
        
        emitter.onTimeout(() -> {
            log.warn("SSE emitter timeout para carrinho {}", cartId);
            emitters.remove(cartId);
        });
        
        emitter.onError((ex) -> {
            log.error("Erro no SSE emitter para carrinho {}: {}", cartId, ex.getMessage());
            emitters.remove(cartId);
        });
        
        emitters.put(cartId, emitter);
        
        // Enviar evento de conexão estabelecida
        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("Conexão estabelecida com sucesso"));
        } catch (IOException e) {
            log.error("Erro ao enviar evento de conexão: {}", e.getMessage());
        }
        
        return emitter;
    }
    
    /**
     * Envia atualização do carrinho para todos os clientes conectados.
     */
    public void sendCartUpdate(UUID cartId, CartResponse cartResponse) {
        SseEmitter emitter = emitters.get(cartId);
        
        if (emitter != null) {
            try {
                log.debug("Enviando atualização do carrinho {} via SSE", cartId);
                emitter.send(SseEmitter.event()
                        .name("cart-updated")
                        .data(cartResponse));
            } catch (IOException e) {
                log.error("Erro ao enviar atualização do carrinho {}: {}", cartId, e.getMessage());
                emitters.remove(cartId);
            }
        }
    }
    
    /**
     * Envia evento de carrinho finalizado e encerra a conexão.
     */
    public void sendCartCompleted(UUID cartId, CartResponse cartResponse) {
        SseEmitter emitter = emitters.get(cartId);
        
        if (emitter != null) {
            try {
                log.info("Enviando evento de carrinho finalizado {} via SSE", cartId);
                emitter.send(SseEmitter.event()
                        .name("cart-completed")
                        .data(cartResponse));
                emitter.complete();
            } catch (IOException e) {
                log.error("Erro ao enviar evento de finalização do carrinho {}: {}", cartId, e.getMessage());
            } finally {
                emitters.remove(cartId);
            }
        }
    }
}
