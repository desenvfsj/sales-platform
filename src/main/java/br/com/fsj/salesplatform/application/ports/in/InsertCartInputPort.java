package br.com.fsj.salesplatform.application.ports.in;

import br.com.fsj.salesplatform.application.core.domain.Cart;

/**
 * Input Port para criação de carrinho.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
public interface InsertCartInputPort {
    
    /**
     * Cria um novo carrinho para o cliente.
     * Se o cliente já tiver um carrinho OPEN, retorna o existente.
     * 
     * @param cart dados do carrinho a ser criado
     * @return carrinho criado ou existente
     */
    Cart insert(Cart cart);
}
