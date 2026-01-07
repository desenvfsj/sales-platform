package br.com.fsj.salesplatform.application.ports.out;

import br.com.fsj.salesplatform.application.core.domain.CartItem;

/**
 * Output Port para atualizar item do carrinho.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
public interface UpdateCartItemOutputPort {
    
    /**
     * Atualiza um item existente do carrinho.
     * 
     * @param item item a ser atualizado
     * @return item atualizado
     */
    CartItem update(CartItem item);
}
