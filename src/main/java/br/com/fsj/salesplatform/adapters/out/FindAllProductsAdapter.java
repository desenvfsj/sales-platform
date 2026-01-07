package br.com.fsj.salesplatform.adapters.out;

import br.com.fsj.salesplatform.adapters.out.repository.ProductRepository;
import br.com.fsj.salesplatform.application.core.domain.Product;
import br.com.fsj.salesplatform.application.ports.out.FindAllProductsOutputPort;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Adapter para buscar todos os produtos.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@Component
public class FindAllProductsAdapter implements FindAllProductsOutputPort {
    
    private final ProductRepository productRepository;
    
    public FindAllProductsAdapter(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }
}
