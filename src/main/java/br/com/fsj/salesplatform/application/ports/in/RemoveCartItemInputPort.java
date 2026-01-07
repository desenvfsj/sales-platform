package br.com.fsj.salesplatform.application.ports.in;

/**
 * Input Port para remover item do carrinho.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
public interface RemoveCartItemInputPort {
    
    /**
     * Remove item do carrinho.
     * 
     * @param itemId ID do item
     */
    void remove(String itemId);
}
