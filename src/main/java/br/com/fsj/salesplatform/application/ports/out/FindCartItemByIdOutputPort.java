package br.com.fsj.salesplatform.application.ports.out;

import br.com.fsj.salesplatform.application.core.domain.CartItem;

import java.util.Optional;

/**
 * Output Port para buscar item do carrinho por ID.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
public interface FindCartItemByIdOutputPort {
    
    /**
     * Busca item do carrinho por ID.
     * 
     * @param itemId ID do item
     * @return Optional com item se encontrado
     */
    Optional<CartItem> find(String itemId);
}
