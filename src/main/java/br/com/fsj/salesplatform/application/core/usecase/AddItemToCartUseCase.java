package br.com.fsj.salesplatform.application.core.usecase;

import br.com.fsj.salesplatform.application.core.domain.Cart;
import br.com.fsj.salesplatform.application.core.domain.CartItem;
import br.com.fsj.salesplatform.application.ports.in.AddItemToCartInputPort;
import br.com.fsj.salesplatform.application.ports.out.FindCartByIdOutputPort;
import br.com.fsj.salesplatform.application.ports.out.FindCartItemByCartAndProductOutputPort;
import br.com.fsj.salesplatform.application.ports.out.InsertCartItemOutputPort;
import br.com.fsj.salesplatform.application.ports.out.UpdateCartItemOutputPort;
import br.com.fsj.salesplatform.exception.BusinessException;
import br.com.fsj.salesplatform.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Use Case para adicionar item ao carrinho.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@Service
public class AddItemToCartUseCase implements AddItemToCartInputPort {
    
    private static final Logger log = LoggerFactory.getLogger(AddItemToCartUseCase.class);
    
    private final FindCartByIdOutputPort findCartByIdOutputPort;
    private final FindCartItemByCartAndProductOutputPort findCartItemByCartAndProductOutputPort;
    private final InsertCartItemOutputPort insertCartItemOutputPort;
    private final UpdateCartItemOutputPort updateCartItemOutputPort;
    
    public AddItemToCartUseCase(FindCartByIdOutputPort findCartByIdOutputPort,
                                FindCartItemByCartAndProductOutputPort findCartItemByCartAndProductOutputPort,
                                InsertCartItemOutputPort insertCartItemOutputPort,
                                UpdateCartItemOutputPort updateCartItemOutputPort) {
        this.findCartByIdOutputPort = findCartByIdOutputPort;
        this.findCartItemByCartAndProductOutputPort = findCartItemByCartAndProductOutputPort;
        this.insertCartItemOutputPort = insertCartItemOutputPort;
        this.updateCartItemOutputPort = updateCartItemOutputPort;
    }
    
    @Override
    public CartItem add(String cartId, CartItem item) {
        log.info("Adicionando item (produto {}) ao carrinho {}", item.productId(), cartId);
        
        Cart cart = findCartByIdOutputPort.find(cartId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Carrinho não encontrado com id: " + cartId));
        
        if (!cart.isOpen()) {
            throw new BusinessException(
                    "Não é possível adicionar itens a carrinho com status: " + cart.status());
        }
        
        // Verificar se produto já existe no carrinho
        var existingItem = findCartItemByCartAndProductOutputPort.find(cartId, item.productId());
        
        if (existingItem.isPresent()) {
            // Atualizar quantidade do item existente
            CartItem existing = existingItem.get();
            int newQuantity = existing.quantity() + item.quantity();
            BigDecimal newSubtotal = existing.unitPrice().multiply(BigDecimal.valueOf(newQuantity));
            
            CartItem updatedItem = new CartItem(
                    existing.id(),
                    existing.cartId(),
                    existing.productId(),
                    existing.productName(),
                    existing.unitPrice(),
                    newQuantity,
                    newSubtotal,
                    existing.totalDiscount(),
                    existing.savedForLater(),
                    existing.discounts(),
                    existing.createdAt(),
                    LocalDateTime.now()
            );
            
            CartItem saved = updateCartItemOutputPort.update(updatedItem);
            
            log.info("Quantidade do item {} atualizada para {}", saved.id(), newQuantity);
            
            return saved;
        }
        
        // Criar novo item
        CartItem saved = insertCartItemOutputPort.insert(item);
        
        log.info("Item {} adicionado ao carrinho {}", saved.id(), cartId);
        
        return saved;
    }
}
