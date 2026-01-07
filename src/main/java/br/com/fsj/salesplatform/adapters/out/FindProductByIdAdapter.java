package br.com.fsj.salesplatform.adapters.out;

import br.com.fsj.salesplatform.adapters.out.repository.ProductRepository;
import br.com.fsj.salesplatform.application.core.domain.Product;
import br.com.fsj.salesplatform.application.ports.out.FindProductByIdOutputPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adapter para buscar produto por ID.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@Component
public class FindProductByIdAdapter implements FindProductByIdOutputPort {
    
    private final ProductRepository productRepository;
    
    public FindProductByIdAdapter(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    @Override
    public Optional<Product> find(String productId) {
        return productRepository.findById(productId);
    }
}
