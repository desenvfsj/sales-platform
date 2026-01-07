package br.com.fsj.salesplatform.service;

import br.com.fsj.salesplatform.dto.CartResponse;
import br.com.fsj.salesplatform.dto.CartSummaryResponse;
import br.com.fsj.salesplatform.dto.CreateCartRequest;
import br.com.fsj.salesplatform.exception.BusinessException;
import br.com.fsj.salesplatform.exception.ResourceNotFoundException;
import br.com.fsj.salesplatform.mapper.CartMapper;
import br.com.fsj.salesplatform.model.Cart;
import br.com.fsj.salesplatform.model.CartStatus;
import br.com.fsj.salesplatform.repository.CartRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service para gerenciamento de carrinhos de compras.
 * 
 * <p>Contém todas as regras de negócio relacionadas a carrinhos.</p>
 * 
 * <p><strong>Regras Implementadas:</strong></p>
 * <ul>
 *   <li>Cliente (identificado por CPF) só pode ter 1 carrinho OPEN por vez</li>
 *   <li>CPF deve conter exatamente 11 dígitos numéricos</li>
 *   <li>Carrinho COMPLETED não pode ser modificado</li>
 *   <li>Totais são calculados automaticamente</li>
 * </ul>
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@Service
public class CartService {
    
    private static final Logger log = LoggerFactory.getLogger(CartService.class);
    
    private final CartRepository cartRepository;
    private final SseEmitterService sseEmitterService;
    private final CartMapper cartMapper;
    
    public CartService(CartRepository cartRepository, SseEmitterService sseEmitterService, CartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.sseEmitterService = sseEmitterService;
        this.cartMapper = cartMapper;
    }
    
    /**
     * Cria um novo carrinho para o cliente.
     * 
     * <p>Se o cliente já tiver um carrinho OPEN, retorna o existente.</p>
     * 
     * @param request dados para criação do carrinho
     * @return carrinho criado ou existente
     */
    @Transactional
    public CartResponse createCart(CreateCartRequest request) {
        log.info("Criando carrinho para cliente CPF {} no canal {}", 
                request.customerCpf(), request.channel());
        
        // Verificar se já existe carrinho aberto
        var existingCart = cartRepository.findByCustomerCpfAndStatus(
                request.customerCpf(), CartStatus.OPEN);
        
        if (existingCart.isPresent()) {
            log.info("Cliente CPF {} já possui carrinho aberto: {}", 
                    request.customerCpf(), existingCart.get().getId());
            return mapToResponse(existingCart.get());
        }
        
        // Criar novo carrinho
        UUID cartId = UUID.fromString(request.id());
        Cart cart = new Cart(cartId, request.customerCpf(), request.channel());
        Cart saved = cartRepository.save(cart);
        
        log.info("Carrinho {} criado com sucesso para cliente CPF {}", 
                saved.getId(), saved.getCustomerCpf());
        
        CartResponse response = mapToResponse(saved);
        
        // Notificar via SSE
        sseEmitterService.sendCartUpdate(saved.getId(), response);
        
        return response;
    }
    
    /**
     * Busca carrinho aberto do cliente por CPF.
     * Carrega itens e descontos associados em duas queries para evitar MultipleBagFetchException.
     * 
     * @param customerCpf CPF do cliente
     * @return carrinho aberto com itens e descontos
     * @throws ResourceNotFoundException se não encontrar carrinho aberto
     */
    @Transactional(readOnly = true)
    public CartResponse getOpenCart(String customerCpf) {
        log.debug("Buscando carrinho aberto do cliente CPF {} com itens e descontos", customerCpf);
        
        Cart cart = cartRepository.findByCustomerCpfAndStatusWithItems(
                        customerCpf, CartStatus.OPEN)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente CPF " + customerCpf + " não possui carrinho aberto"));
        
        // Carregar descontos dos itens (segunda query para evitar MultipleBagFetchException)
        if (cart.getId() != null) {
            cartRepository.findItemsWithDiscountsByCartId(cart.getId());
        }
        
        return mapToResponse(cart);
    }
    
    /**
     * Busca carrinho por ID.
     * Carrega itens e descontos associados em duas queries para evitar MultipleBagFetchException.
     * 
     * @param cartId UUID do carrinho
     * @return carrinho encontrado com itens e descontos
     * @throws ResourceNotFoundException se não encontrar
     */
    @Transactional(readOnly = true)
    public CartResponse getCartById(UUID cartId) {
        log.debug("Buscando carrinho {} com itens e descontos", cartId);
        
        Cart cart = cartRepository.findByIdWithItems(cartId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Carrinho não encontrado com id: " + cartId));
        
        // Carregar descontos dos itens (segunda query para evitar MultipleBagFetchException)
        cartRepository.findItemsWithDiscountsByCartId(cartId);
        
        return mapToResponse(cart);
    }
    
    /**
     * Limpa todos os itens do carrinho.
     * 
     * @param cartId UUID do carrinho
     * @throws ResourceNotFoundException se não encontrar
     * @throws BusinessException se carrinho não estiver ACTIVE
     */
    @Transactional
    public void clearCart(UUID cartId) {
        log.info("Limpando carrinho {}", cartId);
        
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Carrinho não encontrado com id: " + cartId));
        
        if (!cart.isOpen()) {
            throw new BusinessException(
                    "Não é possível limpar carrinho com status: " + cart.getStatus());
        }
        
        cart.getItems().clear();
        cart.calculateTotal();
        Cart saved = cartRepository.save(cart);
        
        log.info("Carrinho {} limpo com sucesso", cartId);
        
        // Notificar via SSE
        CartResponse response = mapToResponse(saved);
        sseEmitterService.sendCartUpdate(cartId, response);
    }
    
    /**
     * Finaliza o carrinho (checkout).
     * 
     * @param cartId UUID do carrinho
     * @return carrinho finalizado
     * @throws ResourceNotFoundException se não encontrar
     * @throws BusinessException se carrinho não estiver ACTIVE ou estiver vazio
     */
    @Transactional
    public CartResponse completeCart(UUID cartId) {
        log.info("Finalizando carrinho {}", cartId);
        
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Carrinho não encontrado com id: " + cartId));
        
        if (!cart.isOpen()) {
            throw new BusinessException(
                    "Não é possível finalizar carrinho com status: " + cart.getStatus());
        }
        
        if (cart.getItems().isEmpty()) {
            throw new BusinessException(
                    "Não é possível finalizar carrinho vazio");
        }
        
        cart.complete();
        Cart saved = cartRepository.save(cart);
        
        log.info("Carrinho {} finalizado com sucesso", cartId);
        
        CartResponse response = mapToResponse(saved);
        
        // Notificar via SSE (evento de finalização)
        sseEmitterService.sendCartCompleted(cartId, response);
        
        return response;
    }
    
    /**
     * Recalcula o total do carrinho.
     * 
     * @param cartId UUID do carrinho
     * @return resumo do carrinho atualizado
     * @throws ResourceNotFoundException se não encontrar
     */
    @Transactional
    public CartSummaryResponse calculateCartTotal(UUID cartId) {
        log.debug("Recalculando total do carrinho {}", cartId);
        
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Carrinho não encontrado com id: " + cartId));
        
        cart.calculateTotal();
        Cart saved = cartRepository.save(cart);
        
        // Notificar via SSE
        CartResponse response = mapToResponse(saved);
        sseEmitterService.sendCartUpdate(cartId, response);
        
        return mapToSummary(saved);
    }
    
    /**
     * Busca resumo do carrinho.
     * 
     * @param cartId UUID do carrinho
     * @return resumo do carrinho
     * @throws ResourceNotFoundException se não encontrar
     */
    @Transactional(readOnly = true)
    public CartSummaryResponse getCartSummary(UUID cartId) {
        log.debug("Buscando resumo do carrinho {}", cartId);
        
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Carrinho não encontrado com id: " + cartId));
        
        return mapToSummary(cart);
    }
    
    /**
     * Mapeia entidade para resposta completa.
     */
    private CartResponse mapToResponse(Cart cart) {
        return cartMapper.toCartResponse(cart);
    }
    
    /**
     * Mapeia entidade para resumo.
     */
    private CartSummaryResponse mapToSummary(Cart cart) {
        return cartMapper.toCartSummaryResponse(cart);
    }
}

