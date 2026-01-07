package br.com.fsj.salesplatform.application.ports.out;

import br.com.fsj.salesplatform.application.core.domain.CartItem;

/**
 * Output Port para inserir item no carrinho.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
public interface InsertCartItemOutputPort {
    
    /**
     * Insere um novo item no carrinho.
     * 
     * @param item item a ser inserido
     * @return item inserido
     */
    CartItem insert(CartItem item);
}
