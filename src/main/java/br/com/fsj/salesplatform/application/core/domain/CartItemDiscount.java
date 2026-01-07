package br.com.fsj.salesplatform.application.core.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Domain object que representa um desconto aplicado a um item do carrinho.
 * 
 * <p>Objeto de domínio puro, sem dependências de frameworks.</p>
 * 
 * <p>Um item pode ter múltiplos descontos de diferentes tipos.</p>
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
public record CartItemDiscount(
    String id,
    String cartItemId,
    DiscountType type,
    BigDecimal baseAmount,
    BigDecimal discountPercentage,
    BigDecimal discountAmount,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    
    /**
     * Construtor compacto com validações.
     */
    public CartItemDiscount {
        if (type == null) {
            throw new IllegalArgumentException("Tipo de desconto é obrigatório");
        }
        if (discountPercentage != null && 
            (discountPercentage.compareTo(BigDecimal.ZERO) < 0 || 
             discountPercentage.compareTo(BigDecimal.valueOf(100)) > 0)) {
            throw new IllegalArgumentException("Percentual de desconto deve estar entre 0 e 100");
        }
    }
}
