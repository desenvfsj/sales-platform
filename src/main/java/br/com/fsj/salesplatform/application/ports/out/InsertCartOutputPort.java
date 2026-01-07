package br.com.fsj.salesplatform.application.ports.out;

import br.com.fsj.salesplatform.application.core.domain.Cart;

/**
 * Output Port para inserir carrinho.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
public interface InsertCartOutputPort {
    
    /**
     * Insere um novo carrinho.
     * 
     * @param cart carrinho a ser inserido
     * @return carrinho inserido
     */
    Cart insert(Cart cart);
}
