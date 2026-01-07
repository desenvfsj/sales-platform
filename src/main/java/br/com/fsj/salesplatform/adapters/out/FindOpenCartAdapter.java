package br.com.fsj.salesplatform.adapters.out;

import br.com.fsj.salesplatform.adapters.out.repository.CartRepository;
import br.com.fsj.salesplatform.adapters.out.repository.entity.CartEntity;
import br.com.fsj.salesplatform.adapters.out.repository.mapper.CartEntityMapper;
import br.com.fsj.salesplatform.application.core.domain.Cart;
import br.com.fsj.salesplatform.application.core.domain.CartStatus;
import br.com.fsj.salesplatform.application.ports.out.FindOpenCartOutputPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adapter para buscar carrinho aberto do cliente.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@Component
public class FindOpenCartAdapter implements FindOpenCartOutputPort {
    
    private final CartRepository cartRepository;
    private final CartEntityMapper cartEntityMapper;
    
    public FindOpenCartAdapter(CartRepository cartRepository, CartEntityMapper cartEntityMapper) {
        this.cartRepository = cartRepository;
        this.cartEntityMapper = cartEntityMapper;
    }
    
    @Override
    public Optional<Cart> find(String customerCpf) {
        Optional<CartEntity> entity = cartRepository.findByCustomerCpfAndStatusWithItems(
                customerCpf, CartStatus.OPEN);
        
        if (entity.isPresent()) {
            // Carregar descontos dos itens (segunda query)
            cartRepository.findItemsWithDiscountsByCartId(entity.get().getId());
        }
        
        return entity.map(cartEntityMapper::toDomain);
    }
}
