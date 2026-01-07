package br.com.fsj.salesplatform.application.core.usecase;

import br.com.fsj.salesplatform.application.core.domain.Cart;
import br.com.fsj.salesplatform.application.ports.in.FindOpenCartInputPort;
import br.com.fsj.salesplatform.application.ports.out.FindOpenCartOutputPort;
import br.com.fsj.salesplatform.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Use Case para buscar carrinho aberto do cliente.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@Service
public class FindOpenCartUseCase implements FindOpenCartInputPort {
    
    private static final Logger log = LoggerFactory.getLogger(FindOpenCartUseCase.class);
    
    private final FindOpenCartOutputPort findOpenCartOutputPort;
    
    public FindOpenCartUseCase(FindOpenCartOutputPort findOpenCartOutputPort) {
        this.findOpenCartOutputPort = findOpenCartOutputPort;
    }
    
    @Override
    public Cart find(String customerCpf) {
        log.debug("Buscando carrinho aberto do cliente CPF {} com itens e descontos", customerCpf);
        
        return findOpenCartOutputPort.find(customerCpf)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente CPF " + customerCpf + " não possui carrinho aberto"));
    }
}
