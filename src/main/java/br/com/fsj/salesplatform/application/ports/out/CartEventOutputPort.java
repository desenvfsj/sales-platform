package br.com.fsj.salesplatform.application.ports.out;

import br.com.fsj.salesplatform.application.core.domain.Cart;

public interface CartEventOutputPort {
    void sendCartUpdate(Cart cart);
    void sendCartCompleted(Cart cart);
}
