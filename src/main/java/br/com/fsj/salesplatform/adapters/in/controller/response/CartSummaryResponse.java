package br.com.fsj.salesplatform.adapters.in.controller.response;

import br.com.fsj.salesplatform.application.core.domain.CartStatus;

import java.math.BigDecimal;

public record CartSummaryResponse(
                String id,
                String customerCpf,
                CartStatus status,
                BigDecimal grossAmount,
                BigDecimal netAmount,
                BigDecimal totalDiscount,
                Integer totalItems) {
}
