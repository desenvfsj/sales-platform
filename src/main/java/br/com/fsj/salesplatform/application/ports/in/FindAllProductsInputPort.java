package br.com.fsj.salesplatform.application.ports.in;

import br.com.fsj.salesplatform.application.core.domain.Product;

import java.util.List;

/**
 * Input Port para listar todos os produtos.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
public interface FindAllProductsInputPort {
    
    /**
     * Lista todos os produtos.
     * 
     * @return lista de produtos
     */
    List<Product> findAll();
}
