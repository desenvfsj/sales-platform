package br.com.fsj.salesplatform.application.core.usecase;

import br.com.fsj.salesplatform.application.core.domain.Product;
import br.com.fsj.salesplatform.application.ports.in.FindProductByIdInputPort;
import br.com.fsj.salesplatform.application.ports.out.FindProductByIdOutputPort;
import br.com.fsj.salesplatform.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Use Case para buscar produto por ID.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@Service
public class FindProductByIdUseCase implements FindProductByIdInputPort {
    
    private static final Logger log = LoggerFactory.getLogger(FindProductByIdUseCase.class);
    
    private final FindProductByIdOutputPort findProductByIdOutputPort;
    
    public FindProductByIdUseCase(FindProductByIdOutputPort findProductByIdOutputPort) {
        this.findProductByIdOutputPort = findProductByIdOutputPort;
    }
    
    @Override
    public Product find(String productId) {
        log.debug("Buscando produto com id: {}", productId);
        
        Product product = findProductByIdOutputPort.find(productId)
                .orElseThrow(() -> {
                    log.warn("Produto não encontrado com id: {}", productId);
                    return new ResourceNotFoundException(
                            "Produto não encontrado com id: " + productId);
                });
        
        log.info("Produto encontrado: {} - {}", product.id(), product.nome());
        
        return product;
    }
}
