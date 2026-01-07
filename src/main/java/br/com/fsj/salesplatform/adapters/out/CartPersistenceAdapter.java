package br.com.fsj.salesplatform.adapters.out;

import br.com.fsj.salesplatform.adapters.out.repository.CartRepository;
import br.com.fsj.salesplatform.adapters.out.repository.entity.CartEntity;
import br.com.fsj.salesplatform.adapters.out.repository.mapper.CartEntityMapper;
import br.com.fsj.salesplatform.application.core.domain.Cart;
import br.com.fsj.salesplatform.application.core.domain.CartStatus;
import br.com.fsj.salesplatform.application.ports.out.CartRepositoryOutputPort;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class CartPersistenceAdapter implements CartRepositoryOutputPort {

    private final CartRepository cartRepository;
    private final CartEntityMapper cartMapper;

    public CartPersistenceAdapter(CartRepository cartRepository, CartEntityMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.cartMapper = cartMapper;
    }

    @Override
    public Cart save(Cart cart) {
        CartEntity entity = cartMapper.toEntity(cart);
        
        // Garantir relacionamento bidirecional para JPA salvar corretamente
        if (entity.getItems() != null) {
            entity.getItems().forEach(item -> {
                item.setCart(entity);
                if (item.getDiscounts() != null) {
                    item.getDiscounts().forEach(discount -> discount.setCartItem(item));
                }
            });
        }
        
        CartEntity saved = cartRepository.save(entity);
        return cartMapper.toDomain(saved);
    }

    @Override
    public Optional<Cart> findById(UUID id) {
        // Carrega Cart e Items
        Optional<CartEntity> entityOpt = cartRepository.findByIdWithItems(id);
        
        if (entityOpt.isPresent()) {
            // Carrega Descontos (fetch additional data na mesma transação)
            cartRepository.findItemsWithDiscountsByCartId(id);
            return Optional.of(cartMapper.toDomain(entityOpt.get()));
        }
        
        return Optional.empty();
    }

    @Override
    public Optional<Cart> findByCustomerCpfAndStatus(String cpf, CartStatus status) {
        Optional<CartEntity> entityOpt = cartRepository.findByCustomerCpfAndStatusWithItems(cpf, status);
        
        if (entityOpt.isPresent()) {
            CartEntity entity = entityOpt.get();
            // Carrega Descontos se tiver ID
            if (entity.getId() != null) {
                cartRepository.findItemsWithDiscountsByCartId(entity.getId());
            }
            return Optional.of(cartMapper.toDomain(entity));
        }
        
        return Optional.empty();
    }

    @Override
    public boolean existsById(UUID id) {
        return cartRepository.existsById(id);
    }
}
