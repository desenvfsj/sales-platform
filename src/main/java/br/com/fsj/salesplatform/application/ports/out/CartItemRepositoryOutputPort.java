package br.com.fsj.salesplatform.application.ports.out;

import br.com.fsj.salesplatform.application.core.domain.CartItem;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartItemRepositoryOutputPort {
    CartItem save(CartItem item);
    void delete(UUID itemId);
    Optional<CartItem> findById(UUID itemId);
    Optional<CartItem> findByCartIdAndProductId(UUID cartId, Long productId);
    List<CartItem> findByCartId(UUID cartId);
}
