package br.com.fsj.salesplatform.adapters.in.controller.mapper;

import br.com.fsj.salesplatform.adapters.in.controller.request.AddItemToCartRequest;
import br.com.fsj.salesplatform.adapters.in.controller.request.CreateCartRequest;
import br.com.fsj.salesplatform.adapters.in.controller.response.CartResponse;
import br.com.fsj.salesplatform.application.core.domain.Cart;
import br.com.fsj.salesplatform.application.core.domain.CartItem;
import br.com.fsj.salesplatform.application.core.domain.CartStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Mapper para conversão entre Domain objects e DTOs de Cart.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@Mapper(componentModel = "spring", uses = {CartItemDTOMapper.class})
public interface CartDTOMapper {
    
    /**
     * Converte CreateCartRequest para Cart (Domain).
     */
    default Cart toDomain(CreateCartRequest request) {
        return new Cart(
                request.id(),
                request.customerCpf(),
                request.channel(),
                CartStatus.OPEN,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                0,
                new ArrayList<>(),
                LocalDateTime.now(),
                null
        );
    }
    
    /**
     * Converte Cart (Domain) para CartResponse.
     */
    @Mapping(target = "totalDiscount", expression = "java(cart.getTotalDiscount())")
    CartResponse toResponse(Cart cart);
    
    /**
     * Converte AddItemToCartRequest para CartItem (Domain).
     */
    default CartItem toCartItemDomain(AddItemToCartRequest request, String cartId) {
        BigDecimal subtotal = request.unitPrice().multiply(BigDecimal.valueOf(request.quantity()));
        
        return new CartItem(
                request.id(),
                cartId,
                request.productId(),
                request.productName(),
                request.unitPrice(),
                request.quantity(),
                subtotal,
                BigDecimal.ZERO,
                false,
                new ArrayList<>(),
                LocalDateTime.now(),
                null
        );
    }
}
