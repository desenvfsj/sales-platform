package br.com.fsj.salesplatform.controller;

import br.com.fsj.salesplatform.dto.CartResponse;
import br.com.fsj.salesplatform.dto.CartSummaryResponse;
import br.com.fsj.salesplatform.dto.CreateCartRequest;
import br.com.fsj.salesplatform.service.CartService;
import br.com.fsj.salesplatform.service.SseEmitterService;
import java.util.UUID;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Controller REST para gerenciamento de carrinhos de compras.
 * 
 * <p>Expõe endpoints para criar, consultar, limpar e finalizar carrinhos.</p>
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/carts")
@Tag(name = "Carrinhos", description = "Gerenciamento de carrinhos de compras multi-canal")
public class CartController {

    private final CartService cartService;
    private final SseEmitterService sseEmitterService;
    
    public CartController(CartService cartService, SseEmitterService sseEmitterService) {
        this.cartService = cartService;
        this.sseEmitterService = sseEmitterService;
    }
    
    @PostMapping
    @Operation(
            summary = "Criar carrinho",
            description = "Cria um novo carrinho para o cliente. Se já existir carrinho ativo, retorna o existente."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Carrinho criado com sucesso",
                    content = @Content(schema = @Schema(implementation = CartResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    public ResponseEntity<CartResponse> createCart(@Valid @RequestBody CreateCartRequest request) {
        CartResponse response = cartService.createCart(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/customer/{customerCpf}/open")
    @Operation(
            summary = "Buscar carrinho aberto",
            description = "Retorna o carrinho aberto do cliente por CPF"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Carrinho encontrado",
                    content = @Content(schema = @Schema(implementation = CartResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Carrinho não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    public ResponseEntity<CartResponse> getOpenCart(
            @PathVariable @Pattern(regexp = "^[0-9]{11}$", message = "CPF deve conter exatamente 11 dígitos")
            String customerCpf) {
        CartResponse response = cartService.getOpenCart(customerCpf);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{cartId}")
    @Operation(
            summary = "Buscar carrinho por ID",
            description = "Retorna os detalhes completos de um carrinho"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Carrinho encontrado",
                    content = @Content(schema = @Schema(implementation = CartResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Carrinho não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    public ResponseEntity<CartResponse> getCartById(@PathVariable UUID cartId) {
        CartResponse response = cartService.getCartById(cartId);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{cartId}")
    @Operation(
            summary = "Limpar carrinho",
            description = "Remove todos os itens do carrinho"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Carrinho limpo com sucesso"),
            @ApiResponse(responseCode = "404", description = "Carrinho não encontrado"),
            @ApiResponse(responseCode = "409", description = "Carrinho não pode ser modificado"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    public ResponseEntity<Void> clearCart(@PathVariable UUID cartId) {
        cartService.clearCart(cartId);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{cartId}/complete")
    @Operation(
            summary = "Finalizar carrinho",
            description = "Finaliza o carrinho (checkout). Carrinho não poderá mais ser modificado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Carrinho finalizado com sucesso",
                    content = @Content(schema = @Schema(implementation = CartResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Carrinho não encontrado"),
            @ApiResponse(responseCode = "409", description = "Carrinho vazio ou já finalizado"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    public ResponseEntity<CartResponse> completeCart(@PathVariable UUID cartId) {
        CartResponse response = cartService.completeCart(cartId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{cartId}/summary")
    @Operation(
            summary = "Resumo do carrinho",
            description = "Retorna resumo do carrinho (totais, sem detalhes dos itens)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Resumo retornado com sucesso",
                    content = @Content(schema = @Schema(implementation = CartSummaryResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Carrinho não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    public ResponseEntity<CartSummaryResponse> getCartSummary(@PathVariable UUID cartId) {
        CartSummaryResponse response = cartService.getCartSummary(cartId);
        return ResponseEntity.ok(response);
    }
    
    @CrossOrigin(origins = "*")
    @GetMapping(value = "/{cartId}/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(
            summary = "Conectar ao stream de eventos do carrinho (SSE)",
            description = """
                    Estabelece uma conexão Server-Sent Events (SSE) para receber atualizações em tempo real do carrinho.
                    
                    Eventos enviados:
                    - 'connected': Confirmação de conexão estabelecida
                    - 'cart-updated': Carrinho foi atualizado (adição/remoção/alteração de itens)
                    - 'cart-completed': Carrinho foi finalizado (checkout)
                    
                    A conexão permanece aberta por até 30 minutos ou até o carrinho ser finalizado.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Conexão SSE estabelecida com sucesso",
                    content = @Content(mediaType = MediaType.TEXT_EVENT_STREAM_VALUE)
            ),
            @ApiResponse(responseCode = "404", description = "Carrinho não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro ao estabelecer conexão")
    })
    public SseEmitter subscribeToCartEvents(@PathVariable UUID cartId) {
        // Valida se o carrinho existe antes de criar a conexão SSE
        cartService.getCartById(cartId);
        
        // Cria e retorna o emitter
        return sseEmitterService.createEmitter(cartId);
    }
}

