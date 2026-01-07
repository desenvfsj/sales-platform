package br.com.fsj.salesplatform.adapters.in.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@RestController
@RequestMapping("/v1/sse")
@Tag(name = "Notificações SSE", description = "Server-Sent Events para atualizações em tempo real")
public class SseController {

    private final SseConnectionManager sseConnectionManager;

    public SseController(SseConnectionManager sseConnectionManager) {
        this.sseConnectionManager = sseConnectionManager;
    }

    @GetMapping(value = "/{cartId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Inscrever em notificações", description = "Abre conexão SSE para receber atualizações do carrinho")
    public SseEmitter subscribe(@PathVariable UUID cartId) {
        return sseConnectionManager.createEmitter(cartId);
    }
}
