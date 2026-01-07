package br.com.fsj.salesplatform.application.ports.in;

import br.com.fsj.salesplatform.application.core.domain.Cart;
import java.util.UUID;

public interface FindCartInputPort {
    Cart findOpenByCpf(String cpf);
    Cart findById(UUID id);
    Cart findSummary(UUID id);
}
