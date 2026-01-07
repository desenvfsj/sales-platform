package br.com.fsj.salesplatform.adapters.in.controller;

import br.com.fsj.salesplatform.adapters.in.controller.mapper.CartMapper;
import br.com.fsj.salesplatform.adapters.in.controller.request.CreateCartRequest;
import br.com.fsj.salesplatform.adapters.in.controller.response.CartResponse;
import br.com.fsj.salesplatform.adapters.in.controller.response.CartSummaryResponse;
import br.com.fsj.salesplatform.application.core.domain.Cart;
import br.com.fsj.salesplatform.application.ports.in.CreateCartInputPort;
import br.com.fsj.salesplatform.application.ports.in.FindCartInputPort;
import br.com.fsj.salesplatform.application.ports.in.ManageCartInputPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/carts")
@Tag(name = "Carrinhos", description = "Gerenciamento de carrinhos de compras")
public class CartController {

    private final CreateCartInputPort createCartInputPort;
    private final FindCartInputPort findCartInputPort;
    private final ManageCartInputPort manageCartInputPort;
    private final CartMapper mapper;

    public CartController(CreateCartInputPort createCartInputPort,
                          FindCartInputPort findCartInputPort,
                          ManageCartInputPort manageCartInputPort,
                          CartMapper mapper) {
        this.createCartInputPort = createCartInputPort;
        this.findCartInputPort = findCartInputPort;
        this.manageCartInputPort = manageCartInputPort;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Criar carrinho", description = "Cria um novo carrinho ou retorna o existente aberto")
    public ResponseEntity<CartResponse> create(@Valid @RequestBody CreateCartRequest request) {
        Cart domain = mapper.toDomain(request);
        Cart created = createCartInputPort.create(domain);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toCartResponse(created));
    }

    @GetMapping("/open/{cpf}")
    @Operation(summary = "Buscar carrinho aberto", description = "Busca o carrinho aberto de um cliente")
    public ResponseEntity<CartResponse> getOpenCart(@PathVariable String cpf) {
        Cart cart = findCartInputPort.findOpenByCpf(cpf);
        return ResponseEntity.ok(mapper.toCartResponse(cart));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar por ID", description = "Busca um carrinho pelo seu ID")
    public ResponseEntity<CartResponse> getById(@PathVariable UUID id) {
        Cart cart = findCartInputPort.findById(id);
        return ResponseEntity.ok(mapper.toCartResponse(cart));
    }

    @GetMapping("/{id}/summary")
    @Operation(summary = "Resumo do carrinho", description = "Retorna apenas os totais do carrinho")
    public ResponseEntity<CartSummaryResponse> getSummary(@PathVariable UUID id) {
        Cart cart = findCartInputPort.findSummary(id);
        return ResponseEntity.ok(mapper.toCartSummaryResponse(cart));
    }

    @DeleteMapping("/{id}/items")
    @Operation(summary = "Limpar carrinho", description = "Remove todos os itens do carrinho")
    public ResponseEntity<Void> clearCart(@PathVariable UUID id) {
        manageCartInputPort.clearCart(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/checkout")
    @Operation(summary = "Finalizar carrinho", description = "Finaliza o carrinho de compras")
    public ResponseEntity<CartResponse> checkout(@PathVariable UUID id) {
        Cart completed = manageCartInputPort.completeCart(id);
        return ResponseEntity.ok(mapper.toCartResponse(completed));
    }
    
    @PostMapping("/{id}/calculate")
    @Operation(summary = "Recalcular totais", description = "Força o recálculo dos totais do carrinho")
    public ResponseEntity<CartSummaryResponse> calculateTotal(@PathVariable UUID id) {
        Cart cart = manageCartInputPort.calculateTotal(id);
        return ResponseEntity.ok(mapper.toCartSummaryResponse(cart));
    }
}
