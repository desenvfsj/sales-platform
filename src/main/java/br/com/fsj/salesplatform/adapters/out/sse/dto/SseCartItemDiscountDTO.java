package br.com.fsj.salesplatform.adapters.out.sse.dto;

import br.com.fsj.salesplatform.application.core.domain.DiscountType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SseCartItemDiscountDTO(
    String id,
    String cartItemId,
    DiscountType type,
    BigDecimal baseAmount,
    BigDecimal discountPercentage,
    BigDecimal discountAmount,
    LocalDateTime createdAt,
    String createdBy,
    LocalDateTime updatedAt,
    String updatedBy
) {}
