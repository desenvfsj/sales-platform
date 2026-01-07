package br.com.fsj.salesplatform.application.core.usecase;

import br.com.fsj.salesplatform.application.core.domain.Product;
import br.com.fsj.salesplatform.application.ports.in.FindProductByIdInputPort;
import br.com.fsj.salesplatform.application.ports.in.ListProductsInputPort;
import br.com.fsj.salesplatform.application.ports.out.ProductRepositoryOutputPort;
import br.com.fsj.salesplatform.application.core.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductUseCase implements ListProductsInputPort, FindProductByIdInputPort {

    private final ProductRepositoryOutputPort productRepository;

    public ProductUseCase(ProductRepositoryOutputPort productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> listAll() {
        return productRepository.findAll();
    }

    @Override
    public Product findById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com id: " + id));
    }
}
