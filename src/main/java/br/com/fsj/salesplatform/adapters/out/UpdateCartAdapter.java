package br.com.fsj.salesplatform.adapters.out;

import br.com.fsj.salesplatform.adapters.out.repository.CartRepository;
import br.com.fsj.salesplatform.adapters.out.repository.entity.CartEntity;
import br.com.fsj.salesplatform.adapters.out.repository.mapper.CartEntityMapper;
import br.com.fsj.salesplatform.application.core.domain.Cart;
import br.com.fsj.salesplatform.application.ports.out.UpdateCartOutputPort;
import org.springframework.stereotype.Component;

/**
 * Adapter para atualizar carrinho no banco de dados.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@Component
public class UpdateCartAdapter implements UpdateCartOutputPort {
    
    private final CartRepository cartRepository;
    private final CartEntityMapper cartEntityMapper;
    
    public UpdateCartAdapter(CartRepository cartRepository, CartEntityMapper cartEntityMapper) {
        this.cartRepository = cartRepository;
        this.cartEntityMapper = cartEntityMapper;
    }
    
    @Override
    public Cart update(Cart cart) {
        CartEntity entity = cartEntityMapper.toEntity(cart);
        CartEntity saved = cartRepository.save(entity);
        return cartEntityMapper.toDomain(saved);
    }
}
