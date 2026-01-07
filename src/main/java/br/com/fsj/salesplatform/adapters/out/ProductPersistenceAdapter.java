package br.com.fsj.salesplatform.adapters.out;

import br.com.fsj.salesplatform.application.core.domain.Product;
import br.com.fsj.salesplatform.application.ports.out.ProductRepositoryOutputPort;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ProductPersistenceAdapter implements ProductRepositoryOutputPort {

    private static final Logger log = LoggerFactory.getLogger(ProductPersistenceAdapter.class);
    private static final String PRODUCTS_JSON_PATH = "data/products.json";
    
    private final List<Product> products;
    private final ObjectMapper objectMapper;

    public ProductPersistenceAdapter() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.products = loadProductsFromJson();
    }

    private List<Product> loadProductsFromJson() {
        try {
            ClassPathResource resource = new ClassPathResource(PRODUCTS_JSON_PATH);
            
            try (InputStream inputStream = resource.getInputStream()) {
                List<Product> loadedProducts = objectMapper.readValue(
                    inputStream, 
                    new TypeReference<List<Product>>() {}
                );
                
                log.info("Carregados {} produtos do arquivo JSON", loadedProducts.size());
                return loadedProducts;
            }
            
        } catch (IOException e) {
            log.error("Erro ao carregar produtos do arquivo JSON: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products);
    }

    @Override
    public Optional<Product> findById(String id) {
        return products.stream()
                .filter(product -> product.getId().equals(id))
                .findFirst();
    }
}
