package br.com.fsj.salesplatform.application.ports.out;

import br.com.fsj.salesplatform.application.core.domain.Cart;

import java.util.Optional;

/**
 * Output Port para buscar carrinho aberto do cliente.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
public interface FindOpenCartOutputPort {
    
    /**
     * Busca carrinho aberto do cliente por CPF.
     * 
     * @param customerCpf CPF do cliente
     * @return Optional com carrinho se encontrado
     */
    Optional<Cart> find(String customerCpf);
}
