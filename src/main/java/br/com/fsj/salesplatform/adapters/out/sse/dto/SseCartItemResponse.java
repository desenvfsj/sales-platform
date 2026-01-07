package br.com.fsj.salesplatform.adapters.out.sse.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SseCartItemResponse(
    String id,
    String cartId,
    Long productId,
    String productName,
    BigDecimal unitPrice,
    Integer quantity,
    BigDecimal subtotal,
    List<SseCartItemDiscountDTO> discounts,
    BigDecimal totalDiscount,
    BigDecimal finalPrice,
    Boolean savedForLater,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
