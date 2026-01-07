package br.com.fsj.salesplatform.application.ports.in;

import br.com.fsj.salesplatform.application.core.domain.CartItem;

/**
 * Input Port para adicionar item ao carrinho.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
public interface AddItemToCartInputPort {
    
    /**
     * Adiciona item ao carrinho.
     * Se o produto já existir, atualiza a quantidade.
     * 
     * @param cartId ID do carrinho
     * @param item dados do item a ser adicionado
     * @return item adicionado
     */
    CartItem add(String cartId, CartItem item);
}
