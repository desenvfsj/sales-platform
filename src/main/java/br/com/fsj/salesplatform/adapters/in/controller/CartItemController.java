package br.com.fsj.salesplatform.adapters.in.controller;

import br.com.fsj.salesplatform.adapters.in.controller.mapper.CartDTOMapper;
import br.com.fsj.salesplatform.adapters.in.controller.mapper.CartItemDTOMapper;
import br.com.fsj.salesplatform.adapters.in.controller.request.AddItemToCartRequest;
import br.com.fsj.salesplatform.adapters.in.controller.request.UpdateCartItemRequest;
import br.com.fsj.salesplatform.adapters.in.controller.response.CartItemResponse;
import br.com.fsj.salesplatform.application.core.domain.CartItem;
import br.com.fsj.salesplatform.application.ports.in.AddItemToCartInputPort;
import br.com.fsj.salesplatform.application.ports.in.RemoveCartItemInputPort;
import br.com.fsj.salesplatform.application.ports.in.UpdateCartItemInputPort;
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

/**
 * Controller REST para gerenciamento de itens do carrinho.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/carts")
@Tag(name = "Itens do Carrinho", description = "Gerenciamento de itens do carrinho de compras")
public class CartItemController {

    private final AddItemToCartInputPort addItemToCartInputPort;
    private final UpdateCartItemInputPort updateCartItemInputPort;
    private final RemoveCartItemInputPort removeCartItemInputPort;
    private final CartDTOMapper cartDTOMapper;
    private final CartItemDTOMapper cartItemDTOMapper;
    
    public CartItemController(AddItemToCartInputPort addItemToCartInputPort,
                              UpdateCartItemInputPort updateCartItemInputPort,
                              RemoveCartItemInputPort removeCartItemInputPort,
                              CartDTOMapper cartDTOMapper,
                              CartItemDTOMapper cartItemDTOMapper) {
        this.addItemToCartInputPort = addItemToCartInputPort;
        this.updateCartItemInputPort = updateCartItemInputPort;
        this.removeCartItemInputPort = removeCartItemInputPort;
        this.cartDTOMapper = cartDTOMapper;
        this.cartItemDTOMapper = cartItemDTOMapper;
    }
    
    @PostMapping("/{cartId}/items")
    @Operation(
            summary = "Adicionar item ao carrinho",
            description = "Adiciona um item ao carrinho. Se o produto já existir, atualiza a quantidade."
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
            @PathVariable String cartId,
            @Valid @RequestBody AddItemToCartRequest request) {
        CartItem item = cartDTOMapper.toCartItemDomain(request, cartId);
        CartItem result = addItemToCartInputPort.add(cartId, item);
        CartItemResponse response = cartItemDTOMapper.toResponse(result);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/items/{itemId}")
    @Operation(
            summary = "Atualizar quantidade do item",
            description = "Atualiza a quantidade de um item do carrinho"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Item atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = CartItemResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Item não encontrado"),
            @ApiResponse(responseCode = "409", description = "Carrinho não pode ser modificado"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    public ResponseEntity<CartItemResponse> updateItemQuantity(
            @PathVariable String itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        CartItem result = updateCartItemInputPort.update(itemId, request.quantity());
        CartItemResponse response = cartItemDTOMapper.toResponse(result);
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
    public ResponseEntity<Void> removeItem(@PathVariable String itemId) {
        removeCartItemInputPort.remove(itemId);
        return ResponseEntity.noContent().build();
    }
}
