package br.com.fsj.salesplatform.service;

import br.com.fsj.salesplatform.dto.ProductResponse;
import br.com.fsj.salesplatform.exception.ResourceNotFoundException;
import br.com.fsj.salesplatform.mapper.ProductMapper;
import br.com.fsj.salesplatform.model.Product;
import br.com.fsj.salesplatform.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service para regras de negócio relacionadas a produtos.
 */
@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository repository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository repository, ProductMapper productMapper) {
        this.repository = repository;
        this.productMapper = productMapper;
    }

    /**
     * Lista todos os produtos.
     * 
     * @return lista de todos os produtos
     */
    public List<ProductResponse> listarProdutos() {
        log.debug("Listando todos os produtos");

        List<Product> allProducts = repository.findAll();
        
        List<ProductResponse> productResponses = allProducts.stream()
                .map(productMapper::toProductResponse)
                .toList();

        log.info("Retornando {} produtos", productResponses.size());

        return productResponses;
    }

    /**
     * Busca um produto por ID.
     * 
     * @param id identificador do produto
     * @return produto encontrado
     * @throws ResourceNotFoundException se o produto não for encontrado
     */
    public ProductResponse buscarPorId(String id) {
        log.debug("Buscando produto com id: {}", id);

        Product product = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Produto não encontrado com id: {}", id);
                    return new ResourceNotFoundException(
                            "Produto não encontrado com id: " + id);
                });

        log.info("Produto encontrado: {} - {}", product.getId(), product.getNome());
        return productMapper.toProductResponse(product);
    }
}

