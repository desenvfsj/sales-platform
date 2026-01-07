package br.com.fsj.salesplatform.model;

/**
 * Enum que representa os canais de venda disponíveis na plataforma.
 * 
 * <p>Utilizado para identificar a origem do carrinho de compras e
 * permitir analytics por canal.</p>
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
public enum CartChannel {
    
    /**
     * Canal Web (site/e-commerce).
     */
    WEB,
    
    /**
     * Canal Mobile (aplicativo móvel).
     */
    MOBILE,
    
    /**
     * Canal PDV (Ponto de Venda físico/loja).
     */
    PDV,
    
    /**
     * Canal API (integrações com parceiros).
     */
    API
}

