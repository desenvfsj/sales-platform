package br.com.fsj.salesplatform.application.ports.out;

import br.com.fsj.salesplatform.application.core.domain.Cart;
import br.com.fsj.salesplatform.application.core.domain.CartStatus;
import java.util.Optional;
import java.util.UUID;

public interface CartRepositoryOutputPort {
    Cart save(Cart cart);
    Optional<Cart> findById(UUID id);
    Optional<Cart> findByCustomerCpfAndStatus(String cpf, CartStatus status);
    boolean existsById(UUID id);
}
