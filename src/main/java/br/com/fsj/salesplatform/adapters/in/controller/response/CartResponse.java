package br.com.fsj.salesplatform.adapters.in.controller.response;

import br.com.fsj.salesplatform.application.core.domain.CartChannel;
import br.com.fsj.salesplatform.application.core.domain.CartStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CartResponse(
                String id,
                String customerCpf,
                CartChannel channel,
                CartStatus status,
                BigDecimal grossAmount,
                BigDecimal netAmount,
                BigDecimal totalDiscount,
                Integer totalItems,
                List<CartItemResponse> items,
                LocalDateTime createdAt,
                LocalDateTime updatedAt) {
}
