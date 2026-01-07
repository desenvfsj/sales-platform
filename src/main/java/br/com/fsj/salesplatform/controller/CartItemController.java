package br.com.fsj.salesplatform.controller;

import br.com.fsj.salesplatform.dto.AddItemToCartRequest;
import br.com.fsj.salesplatform.dto.CartItemResponse;
import br.com.fsj.salesplatform.dto.UpdateCartItemRequest;
import br.com.fsj.salesplatform.service.CartItemService;
import java.util.UUID;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gerenciamento de itens do carrinho.
 * 
 * <p>Expõe endpoints para adicionar, atualizar, remover e listar itens.</p>
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/carts")
@Tag(name = "Itens do Carrinho", description = "Gerenciamento de itens do carrinho de compras")
public class CartItemController {
    
    private final CartItemService cartItemService;
    
    public CartItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }
    
    @PostMapping("/{cartId}/items")
    @Operation(
            summary = "Adicionar item ao carrinho",
            description = "Adiciona um produto ao carrinho. Se o produto já existir, incrementa a quantidade."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Item adicionado com sucesso",
                    content = @Content(schema = @Schema(implementation = CartItemResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Carrinho não encontrado"),
            @ApiResponse(responseCode = "409", description = "Carrinho não pode ser modificado"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    public ResponseEntity<CartItemResponse> addItem(
            @PathVariable UUID cartId,
            @Valid @RequestBody AddItemToCartRequest request) {
        CartItemResponse response = cartItemService.addItem(cartId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{cartId}/items")
    @Operation(
            summary = "Listar itens do carrinho",
            description = "Retorna todos os itens de um carrinho"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de itens retornada com sucesso"
            ),
            @ApiResponse(responseCode = "404", description = "Carrinho não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    public ResponseEntity<List<CartItemResponse>> getCartItems(@PathVariable UUID cartId) {
        List<CartItemResponse> items = cartItemService.getCartItems(cartId);
        return ResponseEntity.ok(items);
    }
    
    @PutMapping("/items/{itemId}")
    @Operation(
            summary = "Atualizar quantidade do item",
            description = "Atualiza a quantidade de um item no carrinho"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Item atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = CartItemResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Quantidade inválida"),
            @ApiResponse(responseCode = "404", description = "Item não encontrado"),
            @ApiResponse(responseCode = "409", description = "Carrinho não pode ser modificado"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    public ResponseEntity<CartItemResponse> updateItem(
            @PathVariable UUID itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        CartItemResponse response = cartItemService.updateItemQuantity(itemId, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/items/{itemId}")
    @Operation(
            summary = "Remover item do carrinho",
            description = "Remove um item do carrinho"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Item removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Item não encontrado"),
            @ApiResponse(responseCode = "409", description = "Carrinho não pode ser modificado"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    public ResponseEntity<Void> removeItem(@PathVariable UUID itemId) {
        cartItemService.removeItem(itemId);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/items/{itemId}/save-for-later")
    @Operation(
            summary = "Salvar item para depois",
            description = "Marca/desmarca item como 'salvar para depois'"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Item atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = CartItemResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Item não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    public ResponseEntity<CartItemResponse> saveForLater(
            @PathVariable UUID itemId,
            @RequestParam(defaultValue = "true") boolean saved) {
        CartItemResponse response = cartItemService.saveForLater(itemId, saved);
        return ResponseEntity.ok(response);
    }
}

