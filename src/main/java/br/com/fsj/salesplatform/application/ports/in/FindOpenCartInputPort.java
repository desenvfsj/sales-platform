package br.com.fsj.salesplatform.application.ports.in;

import br.com.fsj.salesplatform.application.core.domain.Cart;

/**
 * Input Port para buscar carrinho aberto do cliente.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
public interface FindOpenCartInputPort {
    
    /**
     * Busca carrinho aberto do cliente por CPF.
     * 
     * @param customerCpf CPF do cliente
     * @return carrinho aberto com itens e descontos
     */
    Cart find(String customerCpf);
}
