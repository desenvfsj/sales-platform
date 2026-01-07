package br.com.fsj.salesplatform.application.ports.in;

import br.com.fsj.salesplatform.application.core.domain.Product;

/**
 * Input Port para buscar produto por ID.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
public interface FindProductByIdInputPort {
    
    /**
     * Busca um produto por ID.
     * 
     * @param productId ID do produto
     * @return produto encontrado
     */
    Product find(String productId);
}
