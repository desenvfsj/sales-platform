package br.com.fsj.salesplatform.application.core.usecase;

import br.com.fsj.salesplatform.application.core.domain.Cart;
import br.com.fsj.salesplatform.application.ports.in.InsertCartInputPort;
import br.com.fsj.salesplatform.application.ports.out.FindOpenCartOutputPort;
import br.com.fsj.salesplatform.application.ports.out.InsertCartOutputPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Use Case para criação de carrinho.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@Service
public class InsertCartUseCase implements InsertCartInputPort {
    
    private static final Logger log = LoggerFactory.getLogger(InsertCartUseCase.class);
    
    private final InsertCartOutputPort insertCartOutputPort;
    private final FindOpenCartOutputPort findOpenCartOutputPort;
    
    public InsertCartUseCase(InsertCartOutputPort insertCartOutputPort,
                             FindOpenCartOutputPort findOpenCartOutputPort) {
        this.insertCartOutputPort = insertCartOutputPort;
        this.findOpenCartOutputPort = findOpenCartOutputPort;
    }
    
    @Override
    public Cart insert(Cart cart) {
        log.info("Criando carrinho para cliente CPF {} no canal {}", 
                cart.customerCpf(), cart.channel());
        
        // Verificar se já existe carrinho aberto
        var existingCart = findOpenCartOutputPort.find(cart.customerCpf());
        
        if (existingCart.isPresent()) {
            log.info("Cliente CPF {} já possui carrinho aberto: {}", 
                    cart.customerCpf(), existingCart.get().id());
            return existingCart.get();
        }
        
        // Criar novo carrinho
        Cart saved = insertCartOutputPort.insert(cart);
        
        log.info("Carrinho {} criado com sucesso para cliente CPF {}", 
                saved.id(), saved.customerCpf());
        
        return saved;
    }
}
