package br.com.fsj.salesplatform.repository;

import br.com.fsj.salesplatform.model.CartItemDiscount;
import br.com.fsj.salesplatform.model.DiscountType;
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
public interface CartItemDiscountRepository extends JpaRepository<CartItemDiscount, UUID> {

    /**
     * Busca todos os descontos de um item do carrinho.
     *
     * @param cartItemId UUID do item do carrinho
     * @return Lista de descontos do item
     */
    List<CartItemDiscount> findByCartItemId(UUID cartItemId);

    /**
     * Busca descontos de um item por tipo.
     *
     * @param cartItemId UUID do item do carrinho
     * @param type       Tipo do desconto
     * @return Lista de descontos do tipo especificado
     */
    List<CartItemDiscount> findByCartItemIdAndType(UUID cartItemId, DiscountType type);

    /**
     * Calcula o total de descontos aplicados a um item.
     *
     * @param cartItemId UUID do item do carrinho
     * @return Soma total dos descontos
     */
    @Query("SELECT COALESCE(SUM(d.discountAmount), 0) FROM CartItemDiscount d WHERE d.cartItem.id = :cartItemId")
    BigDecimal sumDiscountsByCartItemId(@Param("cartItemId") UUID cartItemId);

    /**
     * Remove todos os descontos de um item do carrinho.
     *
     * @param cartItemId UUID do item do carrinho
     */
    void deleteByCartItemId(UUID cartItemId);

    /**
     * Remove todos os descontos de um tipo específico de um item.
     *
     * @param cartItemId UUID do item do carrinho
     * @param type       Tipo do desconto
     */
    void deleteByCartItemIdAndType(UUID cartItemId, DiscountType type);

    /**
     * Verifica se um item possui descontos.
     *
     * @param cartItemId UUID do item do carrinho
     * @return true se o item possui descontos
     */
    boolean existsByCartItemId(UUID cartItemId);

    /**
     * Verifica se um item possui desconto de um tipo específico.
     *
     * @param cartItemId UUID do item do carrinho
     * @param type       Tipo do desconto
     * @return true se o item possui desconto do tipo especificado
     */
    boolean existsByCartItemIdAndType(UUID cartItemId, DiscountType type);
}
