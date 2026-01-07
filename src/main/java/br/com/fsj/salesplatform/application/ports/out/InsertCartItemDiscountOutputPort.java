package br.com.fsj.salesplatform.application.ports.out;

import br.com.fsj.salesplatform.application.core.domain.CartItemDiscount;

/**
 * Output Port para inserir desconto em item do carrinho.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
public interface InsertCartItemDiscountOutputPort {
    
    /**
     * Insere um novo desconto em um item do carrinho.
     * 
     * @param discount desconto a ser inserido
     * @return desconto inserido
     */
    CartItemDiscount insert(CartItemDiscount discount);
}
