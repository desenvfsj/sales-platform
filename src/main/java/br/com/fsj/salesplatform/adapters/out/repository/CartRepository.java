package br.com.fsj.salesplatform.adapters.out.repository;

import br.com.fsj.salesplatform.adapters.out.repository.entity.CartEntity;
import br.com.fsj.salesplatform.adapters.out.repository.entity.CartItemEntity;
import br.com.fsj.salesplatform.application.core.domain.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, UUID> {
    
    Optional<CartEntity> findByCustomerCpfAndStatus(String customerCpf, CartStatus status);
    
    @Query("SELECT DISTINCT c FROM CartEntity c " +
           "LEFT JOIN FETCH c.items " +
           "WHERE c.id = :id")
    Optional<CartEntity> findByIdWithItems(@Param("id") UUID id);
    
    @Query("SELECT i FROM CartItemEntity i " +
           "LEFT JOIN FETCH i.discounts " +
           "WHERE i.cart.id = :cartId")
    List<CartItemEntity> findItemsWithDiscountsByCartId(@Param("cartId") UUID cartId);
    
    @Query("SELECT DISTINCT c FROM CartEntity c " +
           "LEFT JOIN FETCH c.items " +
           "WHERE c.customerCpf = :customerCpf AND c.status = :status")
    Optional<CartEntity> findByCustomerCpfAndStatusWithItems(
            @Param("customerCpf") String customerCpf, 
            @Param("status") CartStatus status);
}
