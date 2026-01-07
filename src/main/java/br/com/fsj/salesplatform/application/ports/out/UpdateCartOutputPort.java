package br.com.fsj.salesplatform.application.ports.out;

import br.com.fsj.salesplatform.application.core.domain.Cart;

/**
 * Output Port para atualizar carrinho.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
public interface UpdateCartOutputPort {
    
    /**
     * Atualiza um carrinho existente.
     * 
     * @param cart carrinho a ser atualizado
     * @return carrinho atualizado
     */
    Cart update(Cart cart);
}
