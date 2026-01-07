package br.com.fsj.salesplatform.application.ports.in;

/**
 * Input Port para limpar todos os itens do carrinho.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
public interface ClearCartInputPort {
    
    /**
     * Limpa todos os itens do carrinho.
     * 
     * @param cartId ID do carrinho
     */
    void clear(String cartId);
}
