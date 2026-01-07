package br.com.fsj.salesplatform.service;

import br.com.fsj.salesplatform.dto.CartResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Serviço para gerenciamento de conexões SSE (Server-Sent Events).
 *
 * <p>Permite que clientes se inscrevam para receber notificações em tempo real
 * sobre alterações em carrinhos específicos.</p>
 *
 * @author Sales Platform Team
 * @since 1.0.0
 */
@Service
public class SseEmitterService {

    private static final Logger log = LoggerFactory.getLogger(SseEmitterService.class);
    private static final long SSE_TIMEOUT = 30 * 60 * 1000L; // 30 minutos

    // Mapa: cartId -> Lista de emitters conectados
    private final Map<UUID, CopyOnWriteArrayList<SseEmitter>> emitters = new ConcurrentHashMap<>();

    /**
     * Cria uma nova conexão SSE para um carrinho específico.
     *
     * @param cartId UUID do carrinho a ser monitorado
     * @return SseEmitter configurado
     */
    public SseEmitter createEmitter(UUID cartId) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);

        // Adiciona o emitter à lista do carrinho
        emitters.computeIfAbsent(cartId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        log.info("Nova conexão SSE criada para carrinho ID: {}. Total de conexões: {}", cartId, emitters.get(cartId).size());

        // Configura callbacks para limpeza
        emitter.onCompletion(() -> removeEmitter(cartId, emitter));
        emitter.onTimeout(() -> {
            log.warn("Timeout da conexão SSE para carrinho ID: {}", cartId);
            removeEmitter(cartId, emitter);
        });
        emitter.onError(throwable -> {
            log.error("Erro na conexão SSE para carrinho ID: {}", cartId, throwable);
            removeEmitter(cartId, emitter);
        });

        // Envia evento inicial de conexão estabelecida
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

    /**
     * Envia evento de atualização do carrinho para todos os clientes conectados.
     *
     * @param cartId       UUID do carrinho
     * @param cartResponse Dados atualizados do carrinho
     */
    public void sendCartUpdate(UUID cartId, CartResponse cartResponse) {
        CopyOnWriteArrayList<SseEmitter> cartEmitters = emitters.get(cartId);

        if (cartEmitters == null || cartEmitters.isEmpty()) {
            log.debug("Nenhuma conexão SSE ativa para carrinho ID: {}", cartId);
            return;
        }

        log.info("Enviando atualização SSE para carrinho ID: {}. Conexões ativas: {}",
                cartId, cartEmitters.size());

        cartEmitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("cart-updated")
                        .data(cartResponse));

                log.debug("Evento enviado com sucesso para uma conexão do carrinho ID: {}", cartId);
            } catch (Exception e) {
                log.error("Erro ao enviar evento SSE para carrinho ID: {}. Removendo emitter.", cartId, e);
                removeEmitter(cartId, emitter);
            }
        });
    }

    /**
     * Envia evento de finalização do carrinho.
     *
     * @param cartId       UUID do carrinho
     * @param cartResponse Dados finais do carrinho
     */
    public void sendCartCompleted(UUID cartId, CartResponse cartResponse) {
        CopyOnWriteArrayList<SseEmitter> cartEmitters = emitters.get(cartId);

        if (cartEmitters == null || cartEmitters.isEmpty()) {
            log.debug("Nenhuma conexão SSE ativa para carrinho ID: {} (completed)", cartId);
            return;
        }

        log.info("Enviando evento de finalização SSE para carrinho ID: {}", cartId);

        cartEmitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("cart-completed")
                        .data(cartResponse));

                // Completa a conexão após enviar evento de finalização
                emitter.complete();
            } catch (IOException e) {
                log.error("Erro ao enviar evento de finalização SSE para carrinho ID: {}", cartId, e);
            }
        });

        // Remove todos os emitters do carrinho finalizado
        emitters.remove(cartId);
        log.info("Todas as conexões SSE removidas para carrinho finalizado ID: {}", cartId);
    }

    /**
     * Remove um emitter específico da lista de um carrinho.
     *
     * @param cartId  UUID do carrinho
     * @param emitter Emitter a ser removido
     */
    private void removeEmitter(UUID cartId, SseEmitter emitter) {
        CopyOnWriteArrayList<SseEmitter> cartEmitters = emitters.get(cartId);

        if (cartEmitters != null) {
            cartEmitters.remove(emitter);
            log.debug("Emitter removido do carrinho ID: {}. Conexões restantes: {}",
                    cartId, cartEmitters.size());

            // Remove a lista se estiver vazia
            if (cartEmitters.isEmpty()) {
                emitters.remove(cartId);
                log.info("Lista de emitters removida para carrinho ID: {} (vazia)", cartId);
            }
        }
    }

    /**
     * Retorna o número de conexões ativas para um carrinho.
     *
     * @param cartId UUID do carrinho
     * @return Número de conexões ativas
     */
    public int getActiveConnections(UUID cartId) {
        CopyOnWriteArrayList<SseEmitter> cartEmitters = emitters.get(cartId);
        return cartEmitters != null ? cartEmitters.size() : 0;
    }

    /**
     * Retorna o número total de conexões ativas em todos os carrinhos.
     *
     * @return Número total de conexões
     */
    public int getTotalActiveConnections() {
        return emitters.values().stream()
                .mapToInt(CopyOnWriteArrayList::size)
                .sum();
    }
}

