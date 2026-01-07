package br.com.fsj.salesplatform.application.core.usecase;

import br.com.fsj.salesplatform.application.core.domain.Cart;
import br.com.fsj.salesplatform.application.core.domain.CartItem;
import br.com.fsj.salesplatform.application.ports.in.RemoveCartItemInputPort;
import br.com.fsj.salesplatform.application.ports.out.DeleteCartItemOutputPort;
import br.com.fsj.salesplatform.application.ports.out.FindCartByIdOutputPort;
import br.com.fsj.salesplatform.application.ports.out.FindCartItemByIdOutputPort;
import br.com.fsj.salesplatform.exception.BusinessException;
import br.com.fsj.salesplatform.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Use Case para remover item do carrinho.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@Service
public class RemoveCartItemUseCase implements RemoveCartItemInputPort {
    
    private static final Logger log = LoggerFactory.getLogger(RemoveCartItemUseCase.class);
    
    private final FindCartItemByIdOutputPort findCartItemByIdOutputPort;
    private final FindCartByIdOutputPort findCartByIdOutputPort;
    private final DeleteCartItemOutputPort deleteCartItemOutputPort;
    
    public RemoveCartItemUseCase(FindCartItemByIdOutputPort findCartItemByIdOutputPort,
                                 FindCartByIdOutputPort findCartByIdOutputPort,
                                 DeleteCartItemOutputPort deleteCartItemOutputPort) {
        this.findCartItemByIdOutputPort = findCartItemByIdOutputPort;
        this.findCartByIdOutputPort = findCartByIdOutputPort;
        this.deleteCartItemOutputPort = deleteCartItemOutputPort;
    }
    
    @Override
    public void remove(String itemId) {
        log.info("Removendo item {}", itemId);
        
        CartItem item = findCartItemByIdOutputPort.find(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Item do carrinho não encontrado com id: " + itemId));
        
        Cart cart = findCartByIdOutputPort.find(item.cartId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Carrinho não encontrado com id: " + item.cartId()));
        
        if (!cart.isOpen()) {
            throw new BusinessException(
                    "Não é possível remover itens de carrinho com status: " + cart.status());
        }
        
        deleteCartItemOutputPort.delete(itemId);
        
        log.info("Item {} removido com sucesso", itemId);
    }
}
