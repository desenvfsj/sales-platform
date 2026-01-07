package br.com.fsj.salesplatform.application.core.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Domain object que representa um item do carrinho de compras.
 * 
 * <p>Objeto de domínio puro, sem dependências de frameworks.</p>
 * 
 * <p><strong>Regras de Negócio:</strong></p>
 * <ul>
 *   <li>Quantidade deve ser maior que zero</li>
 *   <li>Preço unitário deve ser maior ou igual a zero</li>
 *   <li>Subtotal é calculado automaticamente (unitPrice * quantity)</li>
 *   <li>Não permitir produto duplicado no mesmo carrinho</li>
 * </ul>
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
public record CartItem(
    String id,
    String cartId,
    Long productId,
    String productName,
    BigDecimal unitPrice,
    Integer quantity,
    BigDecimal subtotal,
    BigDecimal totalDiscount,
    Boolean savedForLater,
    List<CartItemDiscount> discounts,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    
    /**
     * Construtor compacto com validações.
     */
    public CartItem {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Preço unitário deve ser maior ou igual a zero");
        }
        if (productName == null || productName.isBlank()) {
            throw new IllegalArgumentException("Nome do produto é obrigatório");
        }
    }
    
    /**
     * Calcula o preço final do item (subtotal - descontos).
     */
    public BigDecimal getFinalPrice() {
        if (subtotal == null || totalDiscount == null) {
            return BigDecimal.ZERO;
        }
        return subtotal.subtract(totalDiscount);
    }
}
