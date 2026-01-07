package br.com.fsj.salesplatform.adapters.out;

import br.com.fsj.salesplatform.adapters.out.repository.CartItemRepository;
import br.com.fsj.salesplatform.adapters.out.repository.entity.CartItemEntity;
import br.com.fsj.salesplatform.adapters.out.repository.mapper.CartItemEntityMapper;
import br.com.fsj.salesplatform.application.core.domain.CartItem;
import br.com.fsj.salesplatform.application.ports.out.FindCartItemByIdOutputPort;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Adapter para buscar item do carrinho por ID.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@Component
public class FindCartItemByIdAdapter implements FindCartItemByIdOutputPort {
    
    private final CartItemRepository cartItemRepository;
    private final CartItemEntityMapper cartItemEntityMapper;
    
    public FindCartItemByIdAdapter(CartItemRepository cartItemRepository,
                                   CartItemEntityMapper cartItemEntityMapper) {
        this.cartItemRepository = cartItemRepository;
        this.cartItemEntityMapper = cartItemEntityMapper;
    }
    
    @Override
    public Optional<CartItem> find(String itemId) {
        UUID uuid = UUID.fromString(itemId);
        Optional<CartItemEntity> entity = cartItemRepository.findById(uuid);
        return entity.map(cartItemEntityMapper::toDomain);
    }
}
