package br.com.fsj.salesplatform.adapters.out.sse;

import br.com.fsj.salesplatform.adapters.out.sse.dto.SseCartResponse;
import br.com.fsj.salesplatform.adapters.out.sse.mapper.SseCartMapper;
import br.com.fsj.salesplatform.application.core.domain.Cart;
import br.com.fsj.salesplatform.application.ports.out.CartEventOutputPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SseEmitterAdapter implements CartEventOutputPort, br.com.fsj.salesplatform.adapters.in.controller.SseConnectionManager {

    private static final Logger log = LoggerFactory.getLogger(SseEmitterAdapter.class);
    private static final long SSE_TIMEOUT = 30 * 60 * 1000L; // 30 minutos

    private final Map<UUID, CopyOnWriteArrayList<SseEmitter>> emitters = new ConcurrentHashMap<>();
    private final SseCartMapper mapper;

    public SseEmitterAdapter(SseCartMapper mapper) {
        this.mapper = mapper;
    }

    public SseEmitter createEmitter(UUID cartId) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);
        emitters.computeIfAbsent(cartId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        log.info("Nova conexão SSE criada para carrinho ID: {}. Total de conexões: {}", cartId, emitters.get(cartId).size());

        emitter.onCompletion(() -> removeEmitter(cartId, emitter));
        emitter.onTimeout(() -> {
            log.warn("Timeout da conexão SSE para carrinho ID: {}", cartId);
            removeEmitter(cartId, emitter);
        });
        emitter.onError(throwable -> {
            log.error("Erro na conexão SSE para carrinho ID: {}", cartId, throwable);
            removeEmitter(cartId, emitter);
        });

        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("Conectado ao carrinho " + cartId));
        } catch (IOException e) {
            log.error("Erro ao enviar evento inicial de conexão para carrinho ID: {}", cartId, e);
            removeEmitter(cartId, emitter);
        }

        return emitter;
    }

    @Override
    public void sendCartUpdate(Cart cart) {
        UUID cartId = cart.getId();
        SseCartResponse response = mapper.toResponse(cart);
        
        CopyOnWriteArrayList<SseEmitter> cartEmitters = emitters.get(cartId);

        if (cartEmitters == null || cartEmitters.isEmpty()) {
            log.debug("Nenhuma conexão SSE ativa para carrinho ID: {}", cartId);
            return;
        }

        log.info("Enviando atualização SSE para carrinho ID: {}. Conexões ativas: {}", cartId, cartEmitters.size());

        cartEmitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("cart-updated")
                        .data(response));
            } catch (Exception e) {
                log.error("Erro ao enviar evento SSE para carrinho ID: {}. Removendo emitter.", cartId, e);
                removeEmitter(cartId, emitter);
            }
        });
    }

    @Override
    public void sendCartCompleted(Cart cart) {
        UUID cartId = cart.getId();
        SseCartResponse response = mapper.toResponse(cart);
        
        CopyOnWriteArrayList<SseEmitter> cartEmitters = emitters.get(cartId);

        if (cartEmitters == null || cartEmitters.isEmpty()) {
            return;
        }

        cartEmitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("cart-completed")
                        .data(response));
                emitter.complete();
            } catch (IOException e) {
                log.error("Erro ao enviar evento de finalização SSE para carrinho ID: {}", cartId, e);
            }
        });

        emitters.remove(cartId);
    }

    private void removeEmitter(UUID cartId, SseEmitter emitter) {
        CopyOnWriteArrayList<SseEmitter> cartEmitters = emitters.get(cartId);
        if (cartEmitters != null) {
            cartEmitters.remove(emitter);
            if (cartEmitters.isEmpty()) {
                emitters.remove(cartId);
            }
        }
    }
}
