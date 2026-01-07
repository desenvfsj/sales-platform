package br.com.fsj.salesplatform.adapters.out.repository;

import br.com.fsj.salesplatform.application.core.domain.Product;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository para acesso aos dados de produtos.
 * 
 * Carrega os dados de um arquivo JSON mockado em resources/data/products.json
 */
@Repository
public class ProductRepository {

    private static final Logger log = LoggerFactory.getLogger(ProductRepository.class);
    private static final String PRODUCTS_JSON_PATH = "data/products.json";
    
    private final List<Product> products;
    private final ObjectMapper objectMapper;

    public ProductRepository() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.products = loadProductsFromJson();
    }

    /**
     * Carrega os produtos do arquivo JSON.
     */
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

    /**
     * Retorna todos os produtos.
     */
    public List<Product> findAll() {
        return new ArrayList<>(products);
    }

    /**
     * Busca um produto por ID.
     */
    public Optional<Product> findById(String id) {
        return products.stream()
                .filter(product -> product.id().equals(id))
                .findFirst();
    }

    /**
     * Busca produtos por nome (contém).
     */
    public List<Product> findByNomeContaining(String nome) {
        return products.stream()
                .filter(product -> product.nome().toLowerCase()
                        .contains(nome.toLowerCase()))
                .toList();
    }

    /**
     * Busca produtos por laboratório.
     */
    public List<Product> findByLaboratorio(String laboratorio) {
        return products.stream()
                .filter(product -> product.laboratorio().equalsIgnoreCase(laboratorio))
                .toList();
    }

    /**
     * Busca produtos por grupo.
     */
    public List<Product> findByGrupo(String grupo) {
        return products.stream()
                .filter(product -> product.grupo().equalsIgnoreCase(grupo))
                .toList();
    }

    /**
     * Busca produtos em promoção.
     */
    public List<Product> findByTemPromocao() {
        return products.stream()
                .filter(product -> Boolean.TRUE.equals(product.temPromocao()))
                .toList();
    }

    /**
     * Conta o total de produtos.
     */
    public long count() {
        return products.size();
    }
}
