package br.com.fsj.salesplatform.application.ports.in;

import br.com.fsj.salesplatform.application.core.domain.Cart;

/**
 * Input Port para finalizar carrinho (checkout).
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
public interface CompleteCartInputPort {
    
    /**
     * Finaliza o carrinho (checkout).
     * 
     * @param cartId ID do carrinho
     * @return carrinho finalizado
     */
    Cart complete(String cartId);
}
