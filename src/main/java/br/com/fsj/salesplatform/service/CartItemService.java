package br.com.fsj.salesplatform.service;

import br.com.fsj.salesplatform.dto.AddItemToCartRequest;
import br.com.fsj.salesplatform.dto.CartItemResponse;
import br.com.fsj.salesplatform.dto.UpdateCartItemRequest;
import br.com.fsj.salesplatform.exception.BusinessException;
import br.com.fsj.salesplatform.exception.ResourceNotFoundException;
import br.com.fsj.salesplatform.mapper.CartMapper;
import br.com.fsj.salesplatform.model.Cart;
import br.com.fsj.salesplatform.model.CartItem;
import br.com.fsj.salesplatform.repository.CartItemRepository;
import br.com.fsj.salesplatform.repository.CartRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service para gerenciamento de itens do carrinho.
 * 
 * <p>
 * Contém todas as regras de negócio relacionadas a itens do carrinho.
 * </p>
 * 
 * <p>
 * <strong>Regras Implementadas:</strong>
 * </p>
 * <ul>
 * <li>Não permitir item duplicado (mesmo product_id)</li>
 * <li>Atualizar total do carrinho automaticamente</li>
 * <li>Validar quantidade > 0</li>
 * <li>Apenas carrinhos ACTIVE podem ser modificados</li>
 * </ul>
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@Service
public class CartItemService {

        private static final Logger log = LoggerFactory.getLogger(CartItemService.class);

        private final CartItemRepository cartItemRepository;
        private final CartRepository cartRepository;
        private final SseEmitterService sseEmitterService;
        private final CartMapper cartMapper;

        public CartItemService(CartItemRepository cartItemRepository,
                        CartRepository cartRepository,
                        CartMapper cartMapper,
                        SseEmitterService sseEmitterService) {
                this.cartItemRepository = cartItemRepository;
                this.cartRepository = cartRepository;
                this.cartMapper = cartMapper;
                this.sseEmitterService = sseEmitterService;
        }

        /**
         * Adiciona item ao carrinho.
         * 
         * <p>
         * Se o produto já existir no carrinho, atualiza a quantidade.
         * </p>
         * 
         * @param cartId  ID do carrinho
         * @param request dados do item
         * @return item adicionado
         * @throws ResourceNotFoundException se carrinho não existir
         * @throws BusinessException         se carrinho não estiver ACTIVE
         */
        @Transactional
        public CartItemResponse addItem(UUID cartId, AddItemToCartRequest request) {
                log.info("Adicionando item (produto {}) ao carrinho {}",
                                request.productId(), cartId);

                Cart cart = cartRepository.findById(cartId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Carrinho não encontrado com id: " + cartId));

                if (!cart.isOpen()) {
                        throw new BusinessException(
                                        "Não é possível adicionar itens a carrinho com status: " + cart.getStatus());
                }

                // Verificar se produto já existe no carrinho
                var existingItem = cartItemRepository.findByCartIdAndProductId(
                                cartId, request.productId());

                if (existingItem.isPresent()) {
                        // Atualizar quantidade do item existente
                        CartItem item = existingItem.get();
                        int newQuantity = item.getQuantity() + request.quantity();
                        item.updateQuantity(newQuantity);
                        CartItem saved = cartItemRepository.save(item);

                        log.info("Quantidade do item {} atualizada para {}",
                                        saved.getId(), newQuantity);

                        // Recalcular total do carrinho
                        cart.calculateTotal();
                        Cart updatedCart = cartRepository.save(cart);

                        // Notificar via SSE
                        notifyCartUpdate(updatedCart);

                        return mapToResponse(saved);
                }

                // Criar novo item
                CartItem item = new CartItem(
                                UUID.fromString(request.id()),
                                request.productId(),
                                request.productName(),
                                request.unitPrice(),
                                request.quantity());

                cart.addItem(item);
                Cart updatedCart = cartRepository.save(cart);

                log.info("Item {} adicionado ao carrinho {}", item.getId(), cartId);

                // Notificar via SSE
                notifyCartUpdate(updatedCart);

                return mapToResponse(item);
        }

        /**
         * Mapeia CartItem para CartItemResponse.
         */
        private CartItemResponse mapToResponse(CartItem item) {
                return cartMapper.toCartItemResponse(item);
        }

        /**
         * Notifica alteração no carrinho via SSE.
         */
        private void notifyCartUpdate(Cart cart) {
                var cartResponse = mapCartToResponse(cart);
                sseEmitterService.sendCartUpdate(cart.getId(), cartResponse);
        }

        /**
         * Mapeia Cart completo para CartResponse.
         */
        private br.com.fsj.salesplatform.dto.CartResponse mapCartToResponse(Cart cart) {
                List<br.com.fsj.salesplatform.dto.CartItemResponse> items = cart.getItems().stream()
                                .map(this::mapToResponse)
                                .toList();

                return new br.com.fsj.salesplatform.dto.CartResponse(
                                cart.getId().toString(),
                                cart.getCustomerCpf(),
                                cart.getChannel(),
                                cart.getStatus(),
                                cart.getGrossAmount(),
                                cart.getNetAmount(),
                                cart.getTotalDiscount(),
                                cart.getTotalItems(),
                                items,
                                cart.getCreatedAt(),
                                cart.getUpdatedAt());
        }

        /**
         * Atualiza quantidade de um item.
         * 
         * @param itemId  ID do item
         * @param request nova quantidade
         * @return item atualizado
         * @throws ResourceNotFoundException se item não existir
         * @throws BusinessException         se carrinho não estiver ACTIVE
         */
        @Transactional
        public CartItemResponse updateItemQuantity(UUID itemId, UpdateCartItemRequest request) {
                log.info("Atualizando quantidade do item {} para {}",
                                itemId, request.quantity());

                CartItem item = cartItemRepository.findById(itemId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Item do carrinho não encontrado com id: " + itemId));

                Cart cart = item.getCart();
                if (!cart.isOpen()) {
                        throw new BusinessException(
                                        "Não é possível atualizar itens de carrinho com status: " + cart.getStatus());
                }

                item.updateQuantity(request.quantity());
                CartItem saved = cartItemRepository.save(item);

                // Recalcular total do carrinho
                cart.calculateTotal();
                Cart updatedCart = cartRepository.save(cart);

                log.info("Quantidade do item {} atualizada com sucesso", itemId);

                // Notificar via SSE
                notifyCartUpdate(updatedCart);

                return mapToResponse(saved);
        }

        /**
         * Remove item do carrinho.
         * 
         * @param itemId ID do item
         * @throws ResourceNotFoundException se item não existir
         * @throws BusinessException         se carrinho não estiver ACTIVE
         */
        @Transactional
        public void removeItem(UUID itemId) {
                log.info("Removendo item {}", itemId);

                CartItem item = cartItemRepository.findById(itemId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Item do carrinho não encontrado com id: " + itemId));

                Cart cart = item.getCart();
                if (!cart.isOpen()) {
                        throw new BusinessException(
                                        "Não é possível remover itens de carrinho com status: " + cart.getStatus());
                }

                cart.removeItem(item);
                cartItemRepository.delete(item);
                Cart updatedCart = cartRepository.save(cart);

                log.info("Item {} removido com sucesso", itemId);

                // Notificar via SSE
                notifyCartUpdate(updatedCart);
        }

        /**
         * Marca/desmarca item como "salvar para depois".
         * 
         * @param itemId ID do item
         * @param saved  true para salvar, false para remover da lista
         * @return item atualizado
         * @throws ResourceNotFoundException se item não existir
         */
        @Transactional
        public CartItemResponse saveForLater(UUID itemId, boolean saved) {
                log.info("Marcando item {} como salvar para depois: {}", itemId, saved);

                CartItem item = cartItemRepository.findById(itemId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Item do carrinho não encontrado com id: " + itemId));

                item.setSavedForLater(saved);
                CartItem updated = cartItemRepository.save(item);

                // Recalcular total do carrinho (itens salvos para depois não contam)
                Cart cart = item.getCart();
                cart.calculateTotal();
                Cart updatedCart = cartRepository.save(cart);

                log.info("Item {} atualizado com sucesso", itemId);

                // Notificar via SSE
                notifyCartUpdate(updatedCart);

                return mapToResponse(updated);
        }

        /**
         * Lista todos os itens de um carrinho.
         * 
         * @param cartId ID do carrinho
         * @return lista de itens
         * @throws ResourceNotFoundException se carrinho não existir
         */
        @Transactional(readOnly = true)
        public List<CartItemResponse> getCartItems(UUID cartId) {
                log.debug("Buscando itens do carrinho {}", cartId);

                // Verificar se carrinho existe
                if (!cartRepository.existsById(cartId)) {
                        throw new ResourceNotFoundException(
                                        "Carrinho não encontrado com id: " + cartId);
                }

                List<CartItem> items = cartItemRepository.findByCartId(cartId);

                return items.stream()
                                .map(this::mapToResponse)
                                .toList();
        }
}
