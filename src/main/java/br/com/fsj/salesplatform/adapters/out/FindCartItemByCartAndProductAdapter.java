package br.com.fsj.salesplatform.adapters.out;

import br.com.fsj.salesplatform.adapters.out.repository.CartItemRepository;
import br.com.fsj.salesplatform.adapters.out.repository.entity.CartItemEntity;
import br.com.fsj.salesplatform.adapters.out.repository.mapper.CartItemEntityMapper;
import br.com.fsj.salesplatform.application.core.domain.CartItem;
import br.com.fsj.salesplatform.application.ports.out.FindCartItemByCartAndProductOutputPort;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Adapter para buscar item do carrinho por carrinho e produto.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@Component
public class FindCartItemByCartAndProductAdapter implements FindCartItemByCartAndProductOutputPort {
    
    private final CartItemRepository cartItemRepository;
    private final CartItemEntityMapper cartItemEntityMapper;
    
    public FindCartItemByCartAndProductAdapter(CartItemRepository cartItemRepository,
                                               CartItemEntityMapper cartItemEntityMapper) {
        this.cartItemRepository = cartItemRepository;
        this.cartItemEntityMapper = cartItemEntityMapper;
    }
    
    @Override
    public Optional<CartItem> find(String cartId, Long productId) {
        UUID uuid = UUID.fromString(cartId);
        Optional<CartItemEntity> entity = cartItemRepository.findByCartIdAndProductId(uuid, productId);
        return entity.map(cartItemEntityMapper::toDomain);
    }
}
