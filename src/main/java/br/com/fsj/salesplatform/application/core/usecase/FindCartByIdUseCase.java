package br.com.fsj.salesplatform.application.core.usecase;

import br.com.fsj.salesplatform.application.core.domain.Cart;
import br.com.fsj.salesplatform.application.ports.in.FindCartByIdInputPort;
import br.com.fsj.salesplatform.application.ports.out.FindCartByIdOutputPort;
import br.com.fsj.salesplatform.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Use Case para buscar carrinho por ID.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@Service
public class FindCartByIdUseCase implements FindCartByIdInputPort {
    
    private static final Logger log = LoggerFactory.getLogger(FindCartByIdUseCase.class);
    
    private final FindCartByIdOutputPort findCartByIdOutputPort;
    
    public FindCartByIdUseCase(FindCartByIdOutputPort findCartByIdOutputPort) {
        this.findCartByIdOutputPort = findCartByIdOutputPort;
    }
    
    @Override
    public Cart find(String cartId) {
        log.debug("Buscando carrinho {} com itens e descontos", cartId);
        
        return findCartByIdOutputPort.find(cartId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Carrinho não encontrado com id: " + cartId));
    }
}
