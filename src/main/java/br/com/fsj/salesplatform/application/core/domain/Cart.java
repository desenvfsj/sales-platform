package br.com.fsj.salesplatform.application.core.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Domain object que representa um carrinho de compras.
 * 
 * <p>Objeto de domínio puro, sem dependências de frameworks.</p>
 * 
 * <p><strong>Regras de Negócio:</strong></p>
 * <ul>
 *   <li>Cliente pode ter apenas 1 carrinho OPEN por vez por canal</li>
 *   <li>CPF deve conter exatamente 11 dígitos numéricos</li>
 *   <li>Total e quantidade são calculados automaticamente</li>
 *   <li>Carrinho COMPLETED não pode ser modificado</li>
 * </ul>
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
public record Cart(
    String id,
    String customerCpf,
    CartChannel channel,
    CartStatus status,
    BigDecimal grossAmount,
    BigDecimal netAmount,
    Integer totalItems,
    List<CartItem> items,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    
    /**
     * Construtor compacto com validações.
     */
    public Cart {
        if (customerCpf == null || !customerCpf.matches("^[0-9]{11}$")) {
            throw new IllegalArgumentException("CPF deve conter exatamente 11 dígitos numéricos");
        }
        if (channel == null) {
            throw new IllegalArgumentException("Canal é obrigatório");
        }
    }
    
    /**
     * Verifica se o carrinho está aberto e pode ser modificado.
     */
    public boolean isOpen() {
        return status == CartStatus.OPEN;
    }
    
    /**
     * Retorna o total de descontos aplicados no carrinho.
     */
    public BigDecimal getTotalDiscount() {
        if (grossAmount == null || netAmount == null) {
            return BigDecimal.ZERO;
        }
        return grossAmount.subtract(netAmount);
    }
}
