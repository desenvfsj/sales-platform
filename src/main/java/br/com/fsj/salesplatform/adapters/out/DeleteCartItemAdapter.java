package br.com.fsj.salesplatform.adapters.out;

import br.com.fsj.salesplatform.adapters.out.repository.CartItemRepository;
import br.com.fsj.salesplatform.application.ports.out.DeleteCartItemOutputPort;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Adapter para deletar item do carrinho.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@Component
public class DeleteCartItemAdapter implements DeleteCartItemOutputPort {
    
    private final CartItemRepository cartItemRepository;
    
    public DeleteCartItemAdapter(CartItemRepository cartItemRepository) {
        this.cartItemRepository = cartItemRepository;
    }
    
    @Override
    public void delete(String itemId) {
        UUID uuid = UUID.fromString(itemId);
        cartItemRepository.deleteById(uuid);
    }
}
