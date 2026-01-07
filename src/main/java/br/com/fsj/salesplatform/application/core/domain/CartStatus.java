package br.com.fsj.salesplatform.application.core.domain;

/**
 * Enum que representa os possíveis status de um carrinho de compras.
 * 
 * <p>Controla o ciclo de vida do carrinho desde sua criação até
 * a finalização, cancelamento ou salvamento para depois.</p>
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
public enum CartStatus {
    
    /**
     * Carrinho aberto - pode ser modificado.
     * Estado inicial de todo carrinho criado.
     */
    OPEN,
    
    /**
     * Carrinho aguardando pagamento - checkout iniciado.
     */
    CHECKOUT_PENDING,
    
    /**
     * Carrinho finalizado - checkout concluído.
     * Não pode mais ser modificado.
     */
    COMPLETED,
    
    /**
     * Carrinho cancelado.
     */
    CANCELLED,
    
    /**
     * Carrinho salvo para depois.
     * Pode ser recuperado pelo cliente posteriormente.
     */
    SAVED
}
