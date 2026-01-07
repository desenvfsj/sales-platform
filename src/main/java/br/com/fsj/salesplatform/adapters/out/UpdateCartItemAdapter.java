package br.com.fsj.salesplatform.adapters.out;

import br.com.fsj.salesplatform.adapters.out.repository.CartItemRepository;
import br.com.fsj.salesplatform.adapters.out.repository.entity.CartItemEntity;
import br.com.fsj.salesplatform.adapters.out.repository.mapper.CartItemEntityMapper;
import br.com.fsj.salesplatform.application.core.domain.CartItem;
import br.com.fsj.salesplatform.application.ports.out.UpdateCartItemOutputPort;
import org.springframework.stereotype.Component;

/**
 * Adapter para atualizar item do carrinho.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@Component
public class UpdateCartItemAdapter implements UpdateCartItemOutputPort {
    
    private final CartItemRepository cartItemRepository;
    private final CartItemEntityMapper cartItemEntityMapper;
    
    public UpdateCartItemAdapter(CartItemRepository cartItemRepository,
                                 CartItemEntityMapper cartItemEntityMapper) {
        this.cartItemRepository = cartItemRepository;
        this.cartItemEntityMapper = cartItemEntityMapper;
    }
    
    @Override
    public CartItem update(CartItem item) {
        CartItemEntity entity = cartItemEntityMapper.toEntity(item);
        CartItemEntity saved = cartItemRepository.save(entity);
        return cartItemEntityMapper.toDomain(saved);
    }
}
