package br.com.fsj.salesplatform.adapters.out;

import br.com.fsj.salesplatform.adapters.out.repository.CartRepository;
import br.com.fsj.salesplatform.adapters.out.repository.entity.CartEntity;
import br.com.fsj.salesplatform.adapters.out.repository.mapper.CartEntityMapper;
import br.com.fsj.salesplatform.application.core.domain.Cart;
import br.com.fsj.salesplatform.application.ports.out.InsertCartOutputPort;
import org.springframework.stereotype.Component;

/**
 * Adapter para inserir carrinho no banco de dados.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@Component
public class InsertCartAdapter implements InsertCartOutputPort {
    
    private final CartRepository cartRepository;
    private final CartEntityMapper cartEntityMapper;
    
    public InsertCartAdapter(CartRepository cartRepository, CartEntityMapper cartEntityMapper) {
        this.cartRepository = cartRepository;
        this.cartEntityMapper = cartEntityMapper;
    }
    
    @Override
    public Cart insert(Cart cart) {
        CartEntity entity = cartEntityMapper.toEntity(cart);
        CartEntity saved = cartRepository.save(entity);
        return cartEntityMapper.toDomain(saved);
    }
}
