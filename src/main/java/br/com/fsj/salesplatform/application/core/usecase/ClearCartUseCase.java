package br.com.fsj.salesplatform.application.core.usecase;

import br.com.fsj.salesplatform.application.core.domain.Cart;
import br.com.fsj.salesplatform.application.ports.in.ClearCartInputPort;
import br.com.fsj.salesplatform.application.ports.out.FindCartByIdOutputPort;
import br.com.fsj.salesplatform.application.ports.out.UpdateCartOutputPort;
import br.com.fsj.salesplatform.exception.BusinessException;
import br.com.fsj.salesplatform.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Use Case para limpar carrinho.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@Service
public class ClearCartUseCase implements ClearCartInputPort {
    
    private static final Logger log = LoggerFactory.getLogger(ClearCartUseCase.class);
    
    private final FindCartByIdOutputPort findCartByIdOutputPort;
    private final UpdateCartOutputPort updateCartOutputPort;
    
    public ClearCartUseCase(FindCartByIdOutputPort findCartByIdOutputPort,
                            UpdateCartOutputPort updateCartOutputPort) {
        this.findCartByIdOutputPort = findCartByIdOutputPort;
        this.updateCartOutputPort = updateCartOutputPort;
    }
    
    @Override
    public void clear(String cartId) {
        log.info("Limpando carrinho {}", cartId);
        
        Cart cart = findCartByIdOutputPort.find(cartId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Carrinho não encontrado com id: " + cartId));
        
        if (!cart.isOpen()) {
            throw new BusinessException(
                    "Não é possível limpar carrinho com status: " + cart.status());
        }
        
        // Criar carrinho limpo
        Cart clearedCart = new Cart(
                cart.id(),
                cart.customerCpf(),
                cart.channel(),
                cart.status(),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                0,
                new ArrayList<>(),
                cart.createdAt(),
                LocalDateTime.now()
        );
        
        updateCartOutputPort.update(clearedCart);
        
        log.info("Carrinho {} limpo com sucesso", cartId);
    }
}
