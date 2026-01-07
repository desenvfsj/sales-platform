package br.com.fsj.salesplatform.application.core.usecase;

import br.com.fsj.salesplatform.application.core.domain.Cart;
import br.com.fsj.salesplatform.application.core.domain.CartItem;
import br.com.fsj.salesplatform.application.core.domain.CartItemDiscount;
import br.com.fsj.salesplatform.application.core.domain.DiscountType;
import br.com.fsj.salesplatform.application.ports.in.ManageCartDiscountsInputPort;
import br.com.fsj.salesplatform.application.ports.out.CartEventOutputPort;
import br.com.fsj.salesplatform.application.ports.out.CartRepositoryOutputPort;
import br.com.fsj.salesplatform.application.core.exception.BusinessException;
import br.com.fsj.salesplatform.application.core.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class CartItemDiscountUseCase implements ManageCartDiscountsInputPort {

    private final CartRepositoryOutputPort cartRepository;
    private final CartEventOutputPort cartEventPort;

    public CartItemDiscountUseCase(CartRepositoryOutputPort cartRepository, CartEventOutputPort cartEventPort) {
        this.cartRepository = cartRepository;
        this.cartEventPort = cartEventPort;
    }

    @Override
    @Transactional
    public CartItemDiscount addDiscount(UUID cartId, UUID itemId, CartItemDiscount discount) {
        Cart cart = findCart(cartId);
        CartItem item = findItemInCart(cart, itemId);
        
        // Validar unicidade de tipo
        boolean exists = item.getDiscounts().stream()
                .anyMatch(d -> d.getType() == discount.getType());
        
        if (exists) {
            throw new BusinessException(
                    String.format("Já existe um desconto do tipo %s para este item.", discount.getType()));
        }

        // Configurar valores
        discount.setBaseAmount(item.getSubtotal());
        if (discount.getDiscountPercentage() == null || discount.getDiscountPercentage().compareTo(BigDecimal.ZERO) == 0) {
            discount.calculateDiscountPercentage();
        } else {
            discount.calculateDiscountAmount();
        }
        
        item.addDiscount(discount);
        
        // Validar e recalcular
        try {
            item.recalculateTotalDiscount();
        } catch (IllegalStateException e) {
            throw new BusinessException(e.getMessage());
        }
        
        cart.calculateTotal();
        
        Cart savedCart = cartRepository.save(cart);
        cartEventPort.sendCartUpdate(savedCart);
        
        // Retornar o desconto salvo (precisaria achar o objeto com ID gerado se for novo, mas aqui o ID vem do input ou é gerado antes)
        // Se o ID for gerado pelo banco, teríamos problema. Mas no request original o ID vinha gerado (UUID).
        return discount;
    }

    @Override
    @Transactional
    public CartItemDiscount updateDiscount(UUID cartId, UUID itemId, UUID discountId, CartItemDiscount discountUpdate) {
        Cart cart = findCart(cartId);
        CartItem item = findItemInCart(cart, itemId);
        
        CartItemDiscount existingDiscount = item.getDiscounts().stream()
                .filter(d -> d.getId().equals(discountId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Desconto não encontrado com id: " + discountId));
        
        // Atualizar valores
        existingDiscount.setDiscountAmount(discountUpdate.getDiscountAmount());
        existingDiscount.setBaseAmount(item.getSubtotal());
        existingDiscount.calculateDiscountPercentage();
        
        try {
            item.recalculateTotalDiscount();
        } catch (IllegalStateException e) {
            throw new BusinessException(e.getMessage());
        }
        
        cart.calculateTotal();
        
        Cart savedCart = cartRepository.save(cart);
        cartEventPort.sendCartUpdate(savedCart);
        
        return existingDiscount;
    }

    @Override
    @Transactional
    public void removeDiscount(UUID cartId, UUID itemId, UUID discountId) {
        Cart cart = findCart(cartId);
        CartItem item = findItemInCart(cart, itemId);
        
        item.getDiscounts().removeIf(d -> d.getId().equals(discountId));
        item.recalculateTotalDiscount();
        cart.calculateTotal();
        
        Cart savedCart = cartRepository.save(cart);
        cartEventPort.sendCartUpdate(savedCart);
    }

    @Override
    @Transactional
    public void removeAllDiscounts(UUID cartId, UUID itemId) {
        Cart cart = findCart(cartId);
        CartItem item = findItemInCart(cart, itemId);
        
        item.getDiscounts().clear();
        item.recalculateTotalDiscount();
        cart.calculateTotal();
        
        Cart savedCart = cartRepository.save(cart);
        cartEventPort.sendCartUpdate(savedCart);
    }

    @Override
    @Transactional
    public void removeDiscountsByType(UUID cartId, UUID itemId, DiscountType type) {
        Cart cart = findCart(cartId);
        CartItem item = findItemInCart(cart, itemId);
        
        item.getDiscounts().removeIf(d -> d.getType() == type);
        item.recalculateTotalDiscount();
        cart.calculateTotal();
        
        Cart savedCart = cartRepository.save(cart);
        cartEventPort.sendCartUpdate(savedCart);
    }

    @Override
    public List<CartItemDiscount> listDiscounts(UUID cartId, UUID itemId) {
        Cart cart = findCart(cartId);
        CartItem item = findItemInCart(cart, itemId);
        return item.getDiscounts();
    }

    @Override
    public CartItemDiscount getDiscount(UUID cartId, UUID itemId, UUID discountId) {
        Cart cart = findCart(cartId);
        CartItem item = findItemInCart(cart, itemId);
        return item.getDiscounts().stream()
                .filter(d -> d.getId().equals(discountId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Desconto não encontrado com id: " + discountId));
    }

    private Cart findCart(UUID cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrinho não encontrado com id: " + cartId));
    }
    
    private CartItem findItemInCart(Cart cart, UUID itemId) {
        return cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado com id: " + itemId));
    }
}
