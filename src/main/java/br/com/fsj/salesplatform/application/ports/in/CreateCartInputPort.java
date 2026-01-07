package br.com.fsj.salesplatform.application.ports.in;

import br.com.fsj.salesplatform.application.core.domain.Cart;

public interface CreateCartInputPort {
    Cart create(Cart cart);
}
