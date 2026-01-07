package br.com.fsj.salesplatform.adapters.out.sse.dto;

import br.com.fsj.salesplatform.application.core.domain.CartChannel;
import br.com.fsj.salesplatform.application.core.domain.CartStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SseCartResponse(
    String id,
    String customerCpf,
    CartChannel channel,
    CartStatus status,
    BigDecimal grossAmount,
    BigDecimal netAmount,
    BigDecimal totalDiscount,
    Integer totalItems,
    List<SseCartItemResponse> items,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
