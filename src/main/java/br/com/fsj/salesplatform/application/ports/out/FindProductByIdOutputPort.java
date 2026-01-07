package br.com.fsj.salesplatform.application.ports.out;

import br.com.fsj.salesplatform.application.core.domain.Product;

import java.util.Optional;

/**
 * Output Port para buscar produto por ID.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
public interface FindProductByIdOutputPort {
    
    /**
     * Busca produto por ID.
     * 
     * @param productId ID do produto
     * @return Optional com produto se encontrado
     */
    Optional<Product> find(String productId);
}
