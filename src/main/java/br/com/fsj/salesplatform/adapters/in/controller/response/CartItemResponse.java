package br.com.fsj.salesplatform.adapters.in.controller.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CartItemResponse(
                String id,
                String cartId,
                Long productId,
                String productName,
                BigDecimal unitPrice,
                Integer quantity,
                BigDecimal subtotal,
                List<CartItemDiscountDTO> discounts,
                BigDecimal totalDiscount,
                BigDecimal finalPrice,
                Boolean savedForLater,
                LocalDateTime createdAt,
                LocalDateTime updatedAt) {
}
