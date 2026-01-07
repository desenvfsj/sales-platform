package br.com.fsj.salesplatform.application.ports.in;

import br.com.fsj.salesplatform.application.core.domain.Cart;

/**
 * Input Port para buscar carrinho por ID.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
public interface FindCartByIdInputPort {
    
    /**
     * Busca carrinho por ID com itens e descontos.
     * 
     * @param cartId ID do carrinho
     * @return carrinho encontrado
     */
    Cart find(String cartId);
}
