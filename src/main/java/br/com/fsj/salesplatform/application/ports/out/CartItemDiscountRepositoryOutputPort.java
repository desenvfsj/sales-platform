package br.com.fsj.salesplatform.application.ports.out;

import br.com.fsj.salesplatform.application.core.domain.CartItemDiscount;
import br.com.fsj.salesplatform.application.core.domain.DiscountType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartItemDiscountRepositoryOutputPort {
    CartItemDiscount save(CartItemDiscount discount);
    void delete(UUID discountId);
    void deleteByCartItemId(UUID itemId);
    void deleteByCartItemIdAndType(UUID itemId, DiscountType type);
    Optional<CartItemDiscount> findById(UUID discountId);
    List<CartItemDiscount> findByCartItemId(UUID itemId);
    boolean existsByCartItemIdAndType(UUID itemId, DiscountType type);
}
