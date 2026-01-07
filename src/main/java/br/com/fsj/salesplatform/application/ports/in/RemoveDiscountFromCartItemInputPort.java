package br.com.fsj.salesplatform.application.ports.in;

/**
 * Input Port para remover desconto de um item do carrinho.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
public interface RemoveDiscountFromCartItemInputPort {
    
    /**
     * Remove desconto de um item do carrinho.
     * 
     * @param discountId ID do desconto
     */
    void remove(String discountId);
}
