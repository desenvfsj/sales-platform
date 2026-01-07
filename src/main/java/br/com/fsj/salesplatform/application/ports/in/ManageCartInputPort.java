package br.com.fsj.salesplatform.application.ports.in;

import br.com.fsj.salesplatform.application.core.domain.Cart;
import java.util.UUID;

public interface ManageCartInputPort {
    void clearCart(UUID cartId);
    Cart completeCart(UUID cartId);
    Cart calculateTotal(UUID cartId);
}
