package br.com.fsj.salesplatform.adapters.out.repository;

import br.com.fsj.salesplatform.adapters.out.repository.entity.CartItemEntity;
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
public interface CartItemRepository extends JpaRepository<CartItemEntity, UUID> {
    
    /**
     * Busca todos os itens de um carrinho.
     */
    List<CartItemEntity> findByCartId(UUID cartId);
    
    /**
     * Busca item específico de um produto em um carrinho.
     */
    Optional<CartItemEntity> findByCartIdAndProductId(UUID cartId, Long productId);
    
    /**
     * Remove todos os itens de um carrinho.
     */
    void deleteByCartId(UUID cartId);
}
