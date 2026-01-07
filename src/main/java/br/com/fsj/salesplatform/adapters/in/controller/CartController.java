package br.com.fsj.salesplatform.adapters.in.controller;

import br.com.fsj.salesplatform.adapters.in.controller.mapper.CartDTOMapper;
import br.com.fsj.salesplatform.adapters.in.controller.request.CreateCartRequest;
import br.com.fsj.salesplatform.adapters.in.controller.response.CartResponse;
import br.com.fsj.salesplatform.application.core.domain.Cart;
import br.com.fsj.salesplatform.application.ports.in.*;
import br.com.fsj.salesplatform.adapters.out.SseEmitterService;
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
 * @author Sales Platform Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/carts")
@Tag(name = "Carrinhos", description = "Gerenciamento de carrinhos de compras multi-canal")
public class CartController {

    private final InsertCartInputPort insertCartInputPort;
    private final FindCartByIdInputPort findCartByIdInputPort;
    private final FindOpenCartInputPort findOpenCartInputPort;
    private final ClearCartInputPort clearCartInputPort;
    private final CompleteCartInputPort completeCartInputPort;
    private final CartDTOMapper cartDTOMapper;
    private final SseEmitterService sseEmitterService;
    
    public CartController(InsertCartInputPort insertCartInputPort,
                          FindCartByIdInputPort findCartByIdInputPort,
                          FindOpenCartInputPort findOpenCartInputPort,
                          ClearCartInputPort clearCartInputPort,
                          CompleteCartInputPort completeCartInputPort,
                          CartDTOMapper cartDTOMapper,
                          SseEmitterService sseEmitterService) {
        this.insertCartInputPort = insertCartInputPort;
        this.findCartByIdInputPort = findCartByIdInputPort;
        this.findOpenCartInputPort = findOpenCartInputPort;
        this.clearCartInputPort = clearCartInputPort;
        this.completeCartInputPort = completeCartInputPort;
        this.cartDTOMapper = cartDTOMapper;
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
        Cart cart = cartDTOMapper.toDomain(request);
        Cart result = insertCartInputPort.insert(cart);
        CartResponse response = cartDTOMapper.toResponse(result);
        
        // Notificar via SSE
        sseEmitterService.sendCartUpdate(java.util.UUID.fromString(result.id()), response);
        
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
        Cart cart = findOpenCartInputPort.find(customerCpf);
        CartResponse response = cartDTOMapper.toResponse(cart);
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
    public ResponseEntity<CartResponse> getCartById(@PathVariable String cartId) {
        Cart cart = findCartByIdInputPort.find(cartId);
        CartResponse response = cartDTOMapper.toResponse(cart);
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
    public ResponseEntity<Void> clearCart(@PathVariable String cartId) {
        clearCartInputPort.clear(cartId);
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
    public ResponseEntity<CartResponse> completeCart(@PathVariable String cartId) {
        Cart cart = completeCartInputPort.complete(cartId);
        CartResponse response = cartDTOMapper.toResponse(cart);
        
        // Notificar via SSE (evento de finalização)
        sseEmitterService.sendCartCompleted(java.util.UUID.fromString(cartId), response);
        
        return ResponseEntity.ok(response);
    }
    
    @CrossOrigin(origins = "*")
    @GetMapping(value = "/{cartId}/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(
            summary = "Conectar ao stream de eventos do carrinho (SSE)",
            description = "Estabelece uma conexão Server-Sent Events (SSE) para receber atualizações em tempo real do carrinho."
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
    public SseEmitter subscribeToCartEvents(@PathVariable String cartId) {
        // Valida se o carrinho existe antes de criar a conexão SSE
        findCartByIdInputPort.find(cartId);
        
        // Cria e retorna o emitter
        return sseEmitterService.createEmitter(java.util.UUID.fromString(cartId));
    }
}
