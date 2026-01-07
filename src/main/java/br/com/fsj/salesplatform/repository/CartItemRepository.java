package br.com.fsj.salesplatform.repository;

import br.com.fsj.salesplatform.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository para acesso a dados de itens do carrinho.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
    
    /**
     * Busca todos os itens de um carrinho.
     * 
     * @param cartId UUID do carrinho
     * @return Lista de itens
     */
    List<CartItem> findByCartId(UUID cartId);
    
    /**
     * Busca item específico de um produto em um carrinho.
     * 
     * @param cartId UUID do carrinho
     * @param productId ID do produto
     * @return Optional com o item encontrado
     */
    Optional<CartItem> findByCartIdAndProductId(UUID cartId, Long productId);
    
    /**
     * Remove todos os itens de um carrinho.
     * 
     * @param cartId UUID do carrinho
     */
    void deleteByCartId(UUID cartId);
}

