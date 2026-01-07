package br.com.fsj.salesplatform.application.ports.out;

import br.com.fsj.salesplatform.application.core.domain.Product;

import java.util.List;

/**
 * Output Port para buscar todos os produtos.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
public interface FindAllProductsOutputPort {
    
    /**
     * Busca todos os produtos.
     * 
     * @return lista de produtos
     */
    List<Product> findAll();
}
