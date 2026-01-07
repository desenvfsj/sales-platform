package br.com.fsj.salesplatform.adapters.in.controller;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.UUID;

public interface SseConnectionManager {
    SseEmitter createEmitter(UUID cartId);
}
