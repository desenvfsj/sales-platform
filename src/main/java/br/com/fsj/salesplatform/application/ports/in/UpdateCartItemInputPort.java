package br.com.fsj.salesplatform.application.ports.in;

import br.com.fsj.salesplatform.application.core.domain.CartItem;

/**
 * Input Port para atualizar quantidade de item do carrinho.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
public interface UpdateCartItemInputPort {
    
    /**
     * Atualiza quantidade de um item.
     * 
     * @param itemId ID do item
     * @param quantity nova quantidade
     * @return item atualizado
     */
    CartItem update(String itemId, Integer quantity);
}
