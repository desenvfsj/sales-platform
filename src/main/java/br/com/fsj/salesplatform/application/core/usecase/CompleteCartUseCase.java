package br.com.fsj.salesplatform.application.core.usecase;

import br.com.fsj.salesplatform.application.core.domain.Cart;
import br.com.fsj.salesplatform.application.core.domain.CartStatus;
import br.com.fsj.salesplatform.application.ports.in.CompleteCartInputPort;
import br.com.fsj.salesplatform.application.ports.out.FindCartByIdOutputPort;
import br.com.fsj.salesplatform.application.ports.out.UpdateCartOutputPort;
import br.com.fsj.salesplatform.exception.BusinessException;
import br.com.fsj.salesplatform.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Use Case para finalizar carrinho (checkout).
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@Service
public class CompleteCartUseCase implements CompleteCartInputPort {
    
    private static final Logger log = LoggerFactory.getLogger(CompleteCartUseCase.class);
    
    private final FindCartByIdOutputPort findCartByIdOutputPort;
    private final UpdateCartOutputPort updateCartOutputPort;
    
    public CompleteCartUseCase(FindCartByIdOutputPort findCartByIdOutputPort,
                               UpdateCartOutputPort updateCartOutputPort) {
        this.findCartByIdOutputPort = findCartByIdOutputPort;
        this.updateCartOutputPort = updateCartOutputPort;
    }
    
    @Override
    public Cart complete(String cartId) {
        log.info("Finalizando carrinho {}", cartId);
        
        Cart cart = findCartByIdOutputPort.find(cartId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Carrinho não encontrado com id: " + cartId));
        
        if (!cart.isOpen()) {
            throw new BusinessException(
                    "Não é possível finalizar carrinho com status: " + cart.status());
        }
        
        if (cart.items() == null || cart.items().isEmpty()) {
            throw new BusinessException(
                    "Não é possível finalizar carrinho vazio");
        }
        
        // Criar carrinho finalizado
        Cart completedCart = new Cart(
                cart.id(),
                cart.customerCpf(),
                cart.channel(),
                CartStatus.COMPLETED,
                cart.grossAmount(),
                cart.netAmount(),
                cart.totalItems(),
                cart.items(),
                cart.createdAt(),
                LocalDateTime.now()
        );
        
        Cart saved = updateCartOutputPort.update(completedCart);
        
        log.info("Carrinho {} finalizado com sucesso", cartId);
        
        return saved;
    }
}
