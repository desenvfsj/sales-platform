package br.com.fsj.salesplatform.controller;

import br.com.fsj.salesplatform.dto.ProductResponse;
import br.com.fsj.salesplatform.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller REST para consulta de produtos.
 * 
 * Expõe endpoints para listagem e busca de produtos farmacêuticos.
 * Todos os dados são mockados e carregados de um arquivo JSON.
 */
@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Produtos", description = "Endpoints para consulta de produtos farmacêuticos")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    /**
     * Lista todos os produtos.
     * 
     * @return lista de todos os produtos
     */
    @Operation(
        summary = "Listar produtos",
        description = "Retorna a lista completa de todos os produtos disponíveis"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de produtos retornada com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProductResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Erro interno do servidor"
        )
    })
    @GetMapping
    public ResponseEntity<List<ProductResponse>> listarProdutos() {
        log.info("GET /api/v1/products");

        List<ProductResponse> products = service.listarProdutos();
        return ResponseEntity.ok(products);
    }

    /**
     * Busca um produto por ID.
     * 
     * @param id identificador do produto
     * @return produto encontrado
     */
    @Operation(
        summary = "Buscar produto por ID",
        description = "Retorna os detalhes de um produto específico pelo seu identificador"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Produto encontrado com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProductResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Produto não encontrado"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Erro interno do servidor"
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> buscarPorId(
            @Parameter(description = "ID do produto", example = "1", required = true)
            @PathVariable String id) {
        
        log.info("GET /api/v1/products/{}", id);

        ProductResponse product = service.buscarPorId(id);
        return ResponseEntity.ok(product);
    }

}

