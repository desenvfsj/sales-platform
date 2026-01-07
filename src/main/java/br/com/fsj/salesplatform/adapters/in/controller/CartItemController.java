package br.com.fsj.salesplatform.adapters.in.controller;

import br.com.fsj.salesplatform.adapters.in.controller.mapper.CartMapper;
import br.com.fsj.salesplatform.adapters.in.controller.request.AddItemToCartRequest;
import br.com.fsj.salesplatform.adapters.in.controller.request.UpdateCartItemRequest;
import br.com.fsj.salesplatform.adapters.in.controller.response.CartItemResponse;
import br.com.fsj.salesplatform.application.core.domain.CartItem;
import br.com.fsj.salesplatform.application.ports.in.ManageCartItemsInputPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/carts/{cartId}/items")
@Tag(name = "Itens do Carrinho", description = "Gerenciamento de itens")
public class CartItemController {

    private final ManageCartItemsInputPort manageCartItemsInputPort;
    private final CartMapper mapper;

    public CartItemController(ManageCartItemsInputPort manageCartItemsInputPort, CartMapper mapper) {
        this.manageCartItemsInputPort = manageCartItemsInputPort;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Adicionar item", description = "Adiciona um item ao carrinho")
    public ResponseEntity<CartItemResponse> addItem(@PathVariable UUID cartId, 
                                                    @Valid @RequestBody AddItemToCartRequest request) {
        CartItem domain = mapper.toDomain(request);
        CartItem added = manageCartItemsInputPort.addItem(cartId, domain);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toCartItemResponse(added));
    }

    @PutMapping("/{itemId}")
    @Operation(summary = "Atualizar quantidade", description = "Atualiza a quantidade de um item")
    public ResponseEntity<CartItemResponse> updateQuantity(@PathVariable UUID cartId,
                                                           @PathVariable UUID itemId,
                                                           @Valid @RequestBody UpdateCartItemRequest request) {
        CartItem updated = manageCartItemsInputPort.updateQuantity(cartId, itemId, request.quantity());
        return ResponseEntity.ok(mapper.toCartItemResponse(updated));
    }

    @DeleteMapping("/{itemId}")
    @Operation(summary = "Remover item", description = "Remove um item do carrinho")
    public ResponseEntity<Void> removeItem(@PathVariable UUID cartId, @PathVariable UUID itemId) {
        manageCartItemsInputPort.removeItem(cartId, itemId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{itemId}/save-for-later")
    @Operation(summary = "Salvar para depois", description = "Marca ou desmarca item para comprar depois")
    public ResponseEntity<CartItemResponse> saveForLater(@PathVariable UUID cartId,
                                                         @PathVariable UUID itemId,
                                                         @RequestParam boolean saved) {
        CartItem updated = manageCartItemsInputPort.saveForLater(cartId, itemId, saved);
        return ResponseEntity.ok(mapper.toCartItemResponse(updated));
    }

    @GetMapping
    @Operation(summary = "Listar itens", description = "Lista todos os itens do carrinho")
    public ResponseEntity<List<CartItemResponse>> listItems(@PathVariable UUID cartId) {
        List<CartItem> items = manageCartItemsInputPort.getItems(cartId);
        List<CartItemResponse> response = items.stream()
                .map(mapper::toCartItemResponse)
                .toList();
        return ResponseEntity.ok(response);
    }
}
