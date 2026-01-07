package br.com.fsj.salesplatform.model;

/**
 * Tipos de desconto permitidos para itens do carrinho.
 */
public enum DiscountType {
    /**
     * Desconto aplicado no balcão/PDV
     */
    BALCAO,
    
    /**
     * Desconto autorizado por gestor
     */
    GESTOR,
    
    /**
     * Desconto de promoção automática
     */
    PROMOCAO,
    
    /**
     * Programa de Benefício de Medicamentos
     */
    PBM,
    
    /**
     * Desconto de Preço Variável
     */
    PV
}

