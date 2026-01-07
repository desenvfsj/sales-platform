package br.com.fsj.salesplatform.application.ports.in;

import br.com.fsj.salesplatform.application.core.domain.CartItemDiscount;
import br.com.fsj.salesplatform.application.core.domain.DiscountType;
import java.util.List;
import java.util.UUID;

public interface ManageCartDiscountsInputPort {
    CartItemDiscount addDiscount(UUID cartId, UUID itemId, CartItemDiscount discount);
    CartItemDiscount updateDiscount(UUID cartId, UUID itemId, UUID discountId, CartItemDiscount discount);
    void removeDiscount(UUID cartId, UUID itemId, UUID discountId);
    void removeAllDiscounts(UUID cartId, UUID itemId);
    void removeDiscountsByType(UUID cartId, UUID itemId, DiscountType type);
    List<CartItemDiscount> listDiscounts(UUID cartId, UUID itemId);
    CartItemDiscount getDiscount(UUID cartId, UUID itemId, UUID discountId);
}
