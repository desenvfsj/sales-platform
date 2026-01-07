package br.com.fsj.salesplatform.adapters.in.controller;

import br.com.fsj.salesplatform.adapters.in.controller.mapper.ProductDTOMapper;
import br.com.fsj.salesplatform.adapters.in.controller.response.ProductResponse;
import br.com.fsj.salesplatform.application.core.domain.Product;
import br.com.fsj.salesplatform.application.ports.in.FindAllProductsInputPort;
import br.com.fsj.salesplatform.application.ports.in.FindProductByIdInputPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gerenciamento de produtos.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Produtos", description = "Consulta de produtos disponíveis")
public class ProductController {

    private final FindAllProductsInputPort findAllProductsInputPort;
    private final FindProductByIdInputPort findProductByIdInputPort;
    private final ProductDTOMapper productDTOMapper;
    
    public ProductController(FindAllProductsInputPort findAllProductsInputPort,
                             FindProductByIdInputPort findProductByIdInputPort,
                             ProductDTOMapper productDTOMapper) {
        this.findAllProductsInputPort = findAllProductsInputPort;
        this.findProductByIdInputPort = findProductByIdInputPort;
        this.productDTOMapper = productDTOMapper;
    }
    
    @GetMapping
    @Operation(
            summary = "Listar todos os produtos",
            description = "Retorna a lista completa de produtos disponíveis"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de produtos retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    public ResponseEntity<List<ProductResponse>> listarProdutos() {
        List<Product> products = findAllProductsInputPort.findAll();
        List<ProductResponse> response = products.stream()
                .map(productDTOMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar produto por ID",
            description = "Retorna os detalhes de um produto específico"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Produto encontrado",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    public ResponseEntity<ProductResponse> buscarPorId(@PathVariable String id) {
        Product product = findProductByIdInputPort.find(id);
        ProductResponse response = productDTOMapper.toResponse(product);
        return ResponseEntity.ok(response);
    }
}
