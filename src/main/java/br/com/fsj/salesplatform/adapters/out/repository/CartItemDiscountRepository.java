package br.com.fsj.salesplatform.adapters.out.repository;

import br.com.fsj.salesplatform.adapters.out.repository.entity.CartItemDiscountEntity;
import br.com.fsj.salesplatform.application.core.domain.DiscountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Repositório para operações de persistência de descontos de itens do carrinho.
 */
@Repository
public interface CartItemDiscountRepository extends JpaRepository<CartItemDiscountEntity, UUID> {

    /**
     * Busca todos os descontos de um item do carrinho.
     */
    List<CartItemDiscountEntity> findByCartItemId(UUID cartItemId);

    /**
     * Busca descontos de um item por tipo.
     */
    List<CartItemDiscountEntity> findByCartItemIdAndType(UUID cartItemId, DiscountType type);

    /**
     * Calcula o total de descontos aplicados a um item.
     */
    @Query("SELECT COALESCE(SUM(d.discountAmount), 0) FROM CartItemDiscountEntity d WHERE d.cartItem.id = :cartItemId")
    BigDecimal sumDiscountsByCartItemId(@Param("cartItemId") UUID cartItemId);

    /**
     * Remove todos os descontos de um item do carrinho.
     */
    void deleteByCartItemId(UUID cartItemId);

    /**
     * Remove todos os descontos de um tipo específico de um item.
     */
    void deleteByCartItemIdAndType(UUID cartItemId, DiscountType type);

    /**
     * Verifica se um item possui descontos.
     */
    boolean existsByCartItemId(UUID cartItemId);

    /**
     * Verifica se um item possui desconto de um tipo específico.
     */
    boolean existsByCartItemIdAndType(UUID cartItemId, DiscountType type);
}
