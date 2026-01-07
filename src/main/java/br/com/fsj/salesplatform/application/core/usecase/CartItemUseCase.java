package br.com.fsj.salesplatform.application.core.usecase;

import br.com.fsj.salesplatform.application.core.domain.Cart;
import br.com.fsj.salesplatform.application.core.domain.CartItem;
import br.com.fsj.salesplatform.application.ports.in.ManageCartItemsInputPort;
import br.com.fsj.salesplatform.application.ports.out.CartEventOutputPort;
import br.com.fsj.salesplatform.application.ports.out.CartRepositoryOutputPort;
import br.com.fsj.salesplatform.application.core.exception.BusinessException;
import br.com.fsj.salesplatform.application.core.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CartItemUseCase implements ManageCartItemsInputPort {

    private final CartRepositoryOutputPort cartRepository;
    private final CartEventOutputPort cartEventPort;

    public CartItemUseCase(CartRepositoryOutputPort cartRepository, CartEventOutputPort cartEventPort) {
        this.cartRepository = cartRepository;
        this.cartEventPort = cartEventPort;
    }

    @Override
    @Transactional
    public CartItem addItem(UUID cartId, CartItem item) {
        Cart cart = findCart(cartId);
        validateCartOpen(cart);

        cart.addItem(item);
        Cart savedCart = cartRepository.save(cart);
        cartEventPort.sendCartUpdate(savedCart);
        
        // Retornar o item salvo (pode ter sido merged)
        return savedCart.getItems().stream()
                .filter(i -> i.getProductId().equals(item.getProductId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Item não encontrado após salvar"));
    }

    @Override
    @Transactional
    public CartItem updateQuantity(UUID cartId, UUID itemId, Integer quantity) {
        Cart cart = findCart(cartId);
        validateCartOpen(cart);
        
        cart.updateItemQuantity(itemId, quantity);
        Cart savedCart = cartRepository.save(cart);
        cartEventPort.sendCartUpdate(savedCart);
        
        return findItemInCart(savedCart, itemId);
    }

    @Override
    @Transactional
    public void removeItem(UUID cartId, UUID itemId) {
        Cart cart = findCart(cartId);
        validateCartOpen(cart);
        
        cart.removeItem(itemId);
        Cart savedCart = cartRepository.save(cart);
        cartEventPort.sendCartUpdate(savedCart);
    }

    @Override
    @Transactional
    public CartItem saveForLater(UUID cartId, UUID itemId, boolean saved) {
        Cart cart = findCart(cartId);
        // Save for later pode ser feito em qualquer status? Original não validava status, mas updateTotal sim.
        // Assumindo que muda o total, deve validar status se for remover do total.
        // O original chamava calculateTotal que soma items nao saved.
        
        cart.setItemSavedForLater(itemId, saved);
        Cart savedCart = cartRepository.save(cart);
        cartEventPort.sendCartUpdate(savedCart);
        
        return findItemInCart(savedCart, itemId);
    }

    @Override
    public List<CartItem> getItems(UUID cartId) {
        Cart cart = findCart(cartId);
        return cart.getItems();
    }

    private Cart findCart(UUID cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrinho não encontrado com id: " + cartId));
    }
    
    private void validateCartOpen(Cart cart) {
        if (!cart.isOpen()) {
            throw new BusinessException("Operação não permitida em carrinho com status: " + cart.getStatus());
        }
    }
    
    private CartItem findItemInCart(Cart cart, UUID itemId) {
        return cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado com id: " + itemId));
    }
}
