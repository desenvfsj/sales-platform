package br.com.fsj.salesplatform.adapters.in.controller;

import br.com.fsj.salesplatform.adapters.in.controller.mapper.ProductMapper;
import br.com.fsj.salesplatform.adapters.in.controller.response.ProductResponse;
import br.com.fsj.salesplatform.application.core.domain.Product;
import br.com.fsj.salesplatform.application.ports.in.FindProductByIdInputPort;
import br.com.fsj.salesplatform.application.ports.in.ListProductsInputPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/products")
@Tag(name = "Produtos", description = "Catálogo de produtos")
public class ProductController {

    private final ListProductsInputPort listProductsInputPort;
    private final FindProductByIdInputPort findProductByIdInputPort;
    private final ProductMapper mapper;

    public ProductController(ListProductsInputPort listProductsInputPort,
                             FindProductByIdInputPort findProductByIdInputPort,
                             ProductMapper mapper) {
        this.listProductsInputPort = listProductsInputPort;
        this.findProductByIdInputPort = findProductByIdInputPort;
        this.mapper = mapper;
    }

    @GetMapping
    @Operation(summary = "Listar produtos", description = "Lista todos os produtos disponíveis")
    public ResponseEntity<List<ProductResponse>> listAll() {
        List<Product> products = listProductsInputPort.listAll();
        List<ProductResponse> response = products.stream()
                .map(mapper::toProductResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar produto", description = "Busca um produto por ID")
    public ResponseEntity<ProductResponse> findById(@PathVariable String id) {
        Product product = findProductByIdInputPort.findById(id);
        return ResponseEntity.ok(mapper.toProductResponse(product));
    }
}
