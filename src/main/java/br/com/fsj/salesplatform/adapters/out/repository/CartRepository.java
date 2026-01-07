package br.com.fsj.salesplatform.adapters.out.repository;

import br.com.fsj.salesplatform.adapters.out.repository.entity.CartEntity;
import br.com.fsj.salesplatform.adapters.out.repository.entity.CartItemEntity;
import br.com.fsj.salesplatform.application.core.domain.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository para acesso a dados de carrinhos.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@Repository
public interface CartRepository extends JpaRepository<CartEntity, UUID> {
    
    /**
     * Busca carrinho de um cliente por CPF e status.
     */
    Optional<CartEntity> findByCustomerCpfAndStatus(String customerCpf, CartStatus status);
    
    /**
     * Busca carrinho por ID com itens carregados.
     */
    @Query("SELECT DISTINCT c FROM CartEntity c " +
           "LEFT JOIN FETCH c.items " +
           "WHERE c.id = :id")
    Optional<CartEntity> findByIdWithItems(@Param("id") UUID id);
    
    /**
     * Carrega os descontos dos itens de um carrinho.
     */
    @Query("SELECT i FROM CartItemEntity i " +
           "LEFT JOIN FETCH i.discounts " +
           "WHERE i.cart.id = :cartId")
    List<CartItemEntity> findItemsWithDiscountsByCartId(@Param("cartId") UUID cartId);
    
    /**
     * Busca carrinho de um cliente por CPF e status com itens carregados.
     */
    @Query("SELECT DISTINCT c FROM CartEntity c " +
           "LEFT JOIN FETCH c.items " +
           "WHERE c.customerCpf = :customerCpf AND c.status = :status")
    Optional<CartEntity> findByCustomerCpfAndStatusWithItems(
            @Param("customerCpf") String customerCpf, 
            @Param("status") CartStatus status);
    
    /**
     * Busca todos os carrinhos de um cliente por CPF.
     */
    List<CartEntity> findByCustomerCpf(String customerCpf);
    
    /**
     * Busca carrinhos inativos (para processo de abandono).
     */
    List<CartEntity> findByStatusAndUpdatedAtBefore(CartStatus status, LocalDateTime date);
}
