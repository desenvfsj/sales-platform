package br.com.fsj.salesplatform.controller;

import br.com.fsj.salesplatform.dto.AddDiscountRequest;
import br.com.fsj.salesplatform.dto.CartItemDiscountDTO;
import br.com.fsj.salesplatform.dto.UpdateDiscountRequest;
import br.com.fsj.salesplatform.model.DiscountType;
import br.com.fsj.salesplatform.service.CartItemDiscountService;
import java.util.UUID;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gerenciar descontos de itens do carrinho.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/carts/{cartId}/items/{itemId}/discounts")
@Tag(name = "Cart Item Discounts", description = "Gerenciamento de descontos de itens do carrinho")
public class CartItemDiscountController {

    private static final Logger log = LoggerFactory.getLogger(CartItemDiscountController.class);

    private final CartItemDiscountService discountService;

    /**
     * Construtor com injeção de dependências.
     */
    public CartItemDiscountController(CartItemDiscountService discountService) {
        this.discountService = discountService;
    }

    @PostMapping
    @Operation(
            summary = "Adicionar desconto a um item",
            description = "Adiciona um novo desconto a um item do carrinho. Um item pode ter múltiplos descontos de diferentes tipos."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Desconto adicionado com sucesso",
                    content = @Content(schema = @Schema(implementation = CartItemDiscountDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Carrinho ou item não encontrado"),
            @ApiResponse(responseCode = "422", description = "Desconto excede o valor do item")
    })
    public ResponseEntity<CartItemDiscountDTO> addDiscount(
            @Parameter(description = "ID do carrinho", required = true)
            @PathVariable UUID cartId,
            
            @Parameter(description = "ID do item", required = true)
            @PathVariable UUID itemId,
            
            @Valid @RequestBody AddDiscountRequest request) {
        
        log.info("POST /api/v1/carts/{}/items/{}/discounts - Adicionando desconto tipo {}", 
                cartId, itemId, request.type());
        
        CartItemDiscountDTO discount = discountService.addDiscount(cartId, itemId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(discount);
    }

    @GetMapping
    @Operation(
            summary = "Listar descontos de um item",
            description = "Retorna todos os descontos aplicados a um item do carrinho"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de descontos retornada com sucesso"
            ),
            @ApiResponse(responseCode = "404", description = "Carrinho ou item não encontrado")
    })
    public ResponseEntity<List<CartItemDiscountDTO>> listDiscounts(
            @Parameter(description = "ID do carrinho", required = true)
            @PathVariable UUID cartId,
            
            @Parameter(description = "ID do item", required = true)
            @PathVariable UUID itemId) {
        
        log.info("GET /api/v1/carts/{}/items/{}/discounts - Listando descontos", cartId, itemId);
        
        List<CartItemDiscountDTO> discounts = discountService.listDiscounts(cartId, itemId);
        return ResponseEntity.ok(discounts);
    }

    @GetMapping("/{discountId}")
    @Operation(
            summary = "Buscar desconto específico",
            description = "Retorna os detalhes de um desconto específico"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Desconto encontrado",
                    content = @Content(schema = @Schema(implementation = CartItemDiscountDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "Desconto não encontrado")
    })
    public ResponseEntity<CartItemDiscountDTO> getDiscount(
            @Parameter(description = "ID do carrinho", required = true)
            @PathVariable UUID cartId,
            
            @Parameter(description = "ID do item", required = true)
            @PathVariable UUID itemId,
            
            @Parameter(description = "ID do desconto", required = true)
            @PathVariable UUID discountId) {
        
        log.info("GET /api/v1/carts/{}/items/{}/discounts/{} - Buscando desconto", 
                cartId, itemId, discountId);
        
        CartItemDiscountDTO discount = discountService.getDiscount(cartId, itemId, discountId);
        return ResponseEntity.ok(discount);
    }

    @PutMapping("/{discountId}")
    @Operation(
            summary = "Atualizar valor de um desconto",
            description = "Atualiza o valor de um desconto existente"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Desconto atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = CartItemDiscountDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Desconto não encontrado"),
            @ApiResponse(responseCode = "422", description = "Novo valor excede o limite permitido")
    })
    public ResponseEntity<CartItemDiscountDTO> updateDiscount(
            @Parameter(description = "ID do carrinho", required = true)
            @PathVariable UUID cartId,
            
            @Parameter(description = "ID do item", required = true)
            @PathVariable UUID itemId,
            
            @Parameter(description = "ID do desconto", required = true)
            @PathVariable UUID discountId,
            
            @Valid @RequestBody UpdateDiscountRequest request) {
        
        log.info("PUT /api/v1/carts/{}/items/{}/discounts/{} - Atualizando desconto", 
                cartId, itemId, discountId);
        
        CartItemDiscountDTO discount = discountService.updateDiscount(cartId, itemId, discountId, request);
        return ResponseEntity.ok(discount);
    }

    @DeleteMapping("/{discountId}")
    @Operation(
            summary = "Remover desconto",
            description = "Remove um desconto específico de um item"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Desconto removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Desconto não encontrado")
    })
    public ResponseEntity<Void> removeDiscount(
            @Parameter(description = "ID do carrinho", required = true)
            @PathVariable UUID cartId,
            
            @Parameter(description = "ID do item", required = true)
            @PathVariable UUID itemId,
            
            @Parameter(description = "ID do desconto", required = true)
            @PathVariable UUID discountId) {
        
        log.info("DELETE /api/v1/carts/{}/items/{}/discounts/{} - Removendo desconto", 
                cartId, itemId, discountId);
        
        discountService.removeDiscount(cartId, itemId, discountId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @Operation(
            summary = "Remover todos os descontos de um item",
            description = "Remove todos os descontos aplicados a um item do carrinho"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Descontos removidos com sucesso"),
            @ApiResponse(responseCode = "404", description = "Carrinho ou item não encontrado")
    })
    public ResponseEntity<Void> removeAllDiscounts(
            @Parameter(description = "ID do carrinho", required = true)
            @PathVariable UUID cartId,
            
            @Parameter(description = "ID do item", required = true)
            @PathVariable UUID itemId) {
        
        log.info("DELETE /api/v1/carts/{}/items/{}/discounts - Removendo todos os descontos", 
                cartId, itemId);
        
        discountService.removeAllDiscounts(cartId, itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/type/{type}")
    @Operation(
            summary = "Remover descontos por tipo",
            description = "Remove todos os descontos de um tipo específico de um item"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Descontos removidos com sucesso"),
            @ApiResponse(responseCode = "404", description = "Carrinho ou item não encontrado")
    })
    public ResponseEntity<Void> removeDiscountsByType(
            @Parameter(description = "ID do carrinho", required = true)
            @PathVariable UUID cartId,
            
            @Parameter(description = "ID do item", required = true)
            @PathVariable UUID itemId,
            
            @Parameter(description = "Tipo do desconto", required = true)
            @PathVariable DiscountType type) {
        
        log.info("DELETE /api/v1/carts/{}/items/{}/discounts/type/{} - Removendo descontos por tipo", 
                cartId, itemId, type);
        
        discountService.removeDiscountsByType(cartId, itemId, type);
        return ResponseEntity.noContent().build();
    }
}

