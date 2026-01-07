package br.com.fsj.salesplatform.application.ports.in;

import br.com.fsj.salesplatform.application.core.domain.CartItem;
import java.util.List;
import java.util.UUID;

public interface ManageCartItemsInputPort {
    CartItem addItem(UUID cartId, CartItem item);
    CartItem updateQuantity(UUID cartId, UUID itemId, Integer quantity);
    void removeItem(UUID cartId, UUID itemId);
    CartItem saveForLater(UUID cartId, UUID itemId, boolean saved);
    List<CartItem> getItems(UUID cartId);
}
