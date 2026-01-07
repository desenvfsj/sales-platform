package br.com.fsj.salesplatform.application.ports.out;

/**
 * Output Port para deletar item do carrinho.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
public interface DeleteCartItemOutputPort {
    
    /**
     * Deleta um item do carrinho.
     * 
     * @param itemId ID do item
     */
    void delete(String itemId);
}
