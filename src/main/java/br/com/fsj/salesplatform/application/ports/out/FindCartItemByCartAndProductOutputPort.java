package br.com.fsj.salesplatform.application.ports.out;

import br.com.fsj.salesplatform.application.core.domain.CartItem;

import java.util.Optional;

/**
 * Output Port para buscar item do carrinho por carrinho e produto.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
public interface FindCartItemByCartAndProductOutputPort {
    
    /**
     * Busca item específico de um produto em um carrinho.
     * 
     * @param cartId ID do carrinho
     * @param productId ID do produto
     * @return Optional com item se encontrado
     */
    Optional<CartItem> find(String cartId, Long productId);
}
