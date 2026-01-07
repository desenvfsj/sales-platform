package br.com.fsj.salesplatform.application.ports.in;

import br.com.fsj.salesplatform.application.core.domain.CartItemDiscount;

/**
 * Input Port para adicionar desconto a um item do carrinho.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
public interface AddDiscountToCartItemInputPort {
    
    /**
     * Adiciona desconto a um item do carrinho.
     * 
     * @param itemId ID do item
     * @param discount dados do desconto
     * @return desconto adicionado
     */
    CartItemDiscount add(String itemId, CartItemDiscount discount);
}
