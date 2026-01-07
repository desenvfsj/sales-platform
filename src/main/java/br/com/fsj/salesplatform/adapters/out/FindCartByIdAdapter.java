package br.com.fsj.salesplatform.adapters.out;

import br.com.fsj.salesplatform.adapters.out.repository.CartRepository;
import br.com.fsj.salesplatform.adapters.out.repository.entity.CartEntity;
import br.com.fsj.salesplatform.adapters.out.repository.mapper.CartEntityMapper;
import br.com.fsj.salesplatform.application.core.domain.Cart;
import br.com.fsj.salesplatform.application.ports.out.FindCartByIdOutputPort;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Adapter para buscar carrinho por ID.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@Component
public class FindCartByIdAdapter implements FindCartByIdOutputPort {
    
    private final CartRepository cartRepository;
    private final CartEntityMapper cartEntityMapper;
    
    public FindCartByIdAdapter(CartRepository cartRepository, CartEntityMapper cartEntityMapper) {
        this.cartRepository = cartRepository;
        this.cartEntityMapper = cartEntityMapper;
    }
    
    @Override
    public Optional<Cart> find(String cartId) {
        UUID uuid = UUID.fromString(cartId);
        
        Optional<CartEntity> entity = cartRepository.findByIdWithItems(uuid);
        
        if (entity.isPresent()) {
            // Carregar descontos dos itens (segunda query)
            cartRepository.findItemsWithDiscountsByCartId(uuid);
        }
        
        return entity.map(cartEntityMapper::toDomain);
    }
}
