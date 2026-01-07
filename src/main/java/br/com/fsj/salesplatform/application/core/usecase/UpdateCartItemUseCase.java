package br.com.fsj.salesplatform.application.core.usecase;

import br.com.fsj.salesplatform.application.core.domain.Cart;
import br.com.fsj.salesplatform.application.core.domain.CartItem;
import br.com.fsj.salesplatform.application.ports.in.UpdateCartItemInputPort;
import br.com.fsj.salesplatform.application.ports.out.FindCartByIdOutputPort;
import br.com.fsj.salesplatform.application.ports.out.FindCartItemByIdOutputPort;
import br.com.fsj.salesplatform.application.ports.out.UpdateCartItemOutputPort;
import br.com.fsj.salesplatform.exception.BusinessException;
import br.com.fsj.salesplatform.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Use Case para atualizar quantidade de item do carrinho.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@Service
public class UpdateCartItemUseCase implements UpdateCartItemInputPort {
    
    private static final Logger log = LoggerFactory.getLogger(UpdateCartItemUseCase.class);
    
    private final FindCartItemByIdOutputPort findCartItemByIdOutputPort;
    private final FindCartByIdOutputPort findCartByIdOutputPort;
    private final UpdateCartItemOutputPort updateCartItemOutputPort;
    
    public UpdateCartItemUseCase(FindCartItemByIdOutputPort findCartItemByIdOutputPort,
                                 FindCartByIdOutputPort findCartByIdOutputPort,
                                 UpdateCartItemOutputPort updateCartItemOutputPort) {
        this.findCartItemByIdOutputPort = findCartItemByIdOutputPort;
        this.findCartByIdOutputPort = findCartByIdOutputPort;
        this.updateCartItemOutputPort = updateCartItemOutputPort;
    }
    
    @Override
    public CartItem update(String itemId, Integer quantity) {
        log.info("Atualizando quantidade do item {} para {}", itemId, quantity);
        
        CartItem item = findCartItemByIdOutputPort.find(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Item do carrinho não encontrado com id: " + itemId));
        
        Cart cart = findCartByIdOutputPort.find(item.cartId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Carrinho não encontrado com id: " + item.cartId()));
        
        if (!cart.isOpen()) {
            throw new BusinessException(
                    "Não é possível atualizar itens de carrinho com status: " + cart.status());
        }
        
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        
        BigDecimal newSubtotal = item.unitPrice().multiply(BigDecimal.valueOf(quantity));
        
        CartItem updatedItem = new CartItem(
                item.id(),
                item.cartId(),
                item.productId(),
                item.productName(),
                item.unitPrice(),
                quantity,
                newSubtotal,
                item.totalDiscount(),
                item.savedForLater(),
                item.discounts(),
                item.createdAt(),
                LocalDateTime.now()
        );
        
        CartItem saved = updateCartItemOutputPort.update(updatedItem);
        
        log.info("Quantidade do item {} atualizada com sucesso", itemId);
        
        return saved;
    }
}
