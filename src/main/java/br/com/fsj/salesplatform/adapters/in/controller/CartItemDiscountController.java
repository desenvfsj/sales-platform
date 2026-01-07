package br.com.fsj.salesplatform.adapters.in.controller;

import br.com.fsj.salesplatform.adapters.in.controller.mapper.CartItemDiscountMapper;
import br.com.fsj.salesplatform.adapters.in.controller.request.AddDiscountRequest;
import br.com.fsj.salesplatform.adapters.in.controller.request.UpdateDiscountRequest;
import br.com.fsj.salesplatform.adapters.in.controller.response.CartItemDiscountDTO;
import br.com.fsj.salesplatform.application.core.domain.CartItemDiscount;
import br.com.fsj.salesplatform.application.core.domain.DiscountType;
import br.com.fsj.salesplatform.application.ports.in.ManageCartDiscountsInputPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/carts/{cartId}/items/{itemId}/discounts")
@Tag(name = "Descontos do Item", description = "Gerenciamento de descontos")
public class CartItemDiscountController {

    private final ManageCartDiscountsInputPort manageCartDiscountsInputPort;
    private final CartItemDiscountMapper mapper;

    public CartItemDiscountController(ManageCartDiscountsInputPort manageCartDiscountsInputPort,
                                      CartItemDiscountMapper mapper) {
        this.manageCartDiscountsInputPort = manageCartDiscountsInputPort;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Adicionar desconto", description = "Adiciona um desconto ao item")
    public ResponseEntity<CartItemDiscountDTO> addDiscount(@PathVariable UUID cartId,
                                                           @PathVariable UUID itemId,
                                                           @Valid @RequestBody AddDiscountRequest request) {
        CartItemDiscount domain = mapper.toDomain(request);
        CartItemDiscount added = manageCartDiscountsInputPort.addDiscount(cartId, itemId, domain);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDTO(added));
    }

    @PutMapping("/{discountId}")
    @Operation(summary = "Atualizar desconto", description = "Atualiza o valor de um desconto")
    public ResponseEntity<CartItemDiscountDTO> updateDiscount(@PathVariable UUID cartId,
                                                              @PathVariable UUID itemId,
                                                              @PathVariable UUID discountId,
                                                              @Valid @RequestBody UpdateDiscountRequest request) {
        CartItemDiscount domain = mapper.toDomain(request);
        CartItemDiscount updated = manageCartDiscountsInputPort.updateDiscount(cartId, itemId, discountId, domain);
        return ResponseEntity.ok(mapper.toDTO(updated));
    }

    @DeleteMapping("/{discountId}")
    @Operation(summary = "Remover desconto", description = "Remove um desconto específico")
    public ResponseEntity<Void> removeDiscount(@PathVariable UUID cartId,
                                               @PathVariable UUID itemId,
                                               @PathVariable UUID discountId) {
        manageCartDiscountsInputPort.removeDiscount(cartId, itemId, discountId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @Operation(summary = "Remover todos os descontos", description = "Remove todos os descontos do item")
    public ResponseEntity<Void> removeAllDiscounts(@PathVariable UUID cartId,
                                                   @PathVariable UUID itemId) {
        manageCartDiscountsInputPort.removeAllDiscounts(cartId, itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/type/{type}")
    @Operation(summary = "Remover descontos por tipo", description = "Remove todos os descontos de um tipo específico")
    public ResponseEntity<Void> removeDiscountsByType(@PathVariable UUID cartId,
                                                      @PathVariable UUID itemId,
                                                      @PathVariable DiscountType type) {
        manageCartDiscountsInputPort.removeDiscountsByType(cartId, itemId, type);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Listar descontos", description = "Lista todos os descontos do item")
    public ResponseEntity<List<CartItemDiscountDTO>> listDiscounts(@PathVariable UUID cartId,
                                                                   @PathVariable UUID itemId) {
        List<CartItemDiscount> discounts = manageCartDiscountsInputPort.listDiscounts(cartId, itemId);
        return ResponseEntity.ok(mapper.toDTOList(discounts));
    }
}
