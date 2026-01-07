package br.com.fsj.salesplatform.application.core.usecase;

import br.com.fsj.salesplatform.application.core.domain.Product;
import br.com.fsj.salesplatform.application.ports.in.FindAllProductsInputPort;
import br.com.fsj.salesplatform.application.ports.out.FindAllProductsOutputPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Use Case para listar todos os produtos.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@Service
public class FindAllProductsUseCase implements FindAllProductsInputPort {
    
    private static final Logger log = LoggerFactory.getLogger(FindAllProductsUseCase.class);
    
    private final FindAllProductsOutputPort findAllProductsOutputPort;
    
    public FindAllProductsUseCase(FindAllProductsOutputPort findAllProductsOutputPort) {
        this.findAllProductsOutputPort = findAllProductsOutputPort;
    }
    
    @Override
    public List<Product> findAll() {
        log.debug("Listando todos os produtos");
        
        List<Product> products = findAllProductsOutputPort.findAll();
        
        log.info("Retornando {} produtos", products.size());
        
        return products;
    }
}
