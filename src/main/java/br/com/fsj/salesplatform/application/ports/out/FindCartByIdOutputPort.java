package br.com.fsj.salesplatform.application.ports.out;

import br.com.fsj.salesplatform.application.core.domain.Cart;

import java.util.Optional;

/**
 * Output Port para buscar carrinho por ID.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
public interface FindCartByIdOutputPort {
    
    /**
     * Busca carrinho por ID.
     * 
     * @param cartId ID do carrinho
     * @return Optional com carrinho se encontrado
     */
    Optional<Cart> find(String cartId);
}
