package br.com.fsj.salesplatform.application.ports.out;

/**
 * Output Port para deletar desconto de item do carrinho.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
public interface DeleteCartItemDiscountOutputPort {
    
    /**
     * Deleta um desconto de um item do carrinho.
     * 
     * @param discountId ID do desconto
     */
    void delete(String discountId);
}
