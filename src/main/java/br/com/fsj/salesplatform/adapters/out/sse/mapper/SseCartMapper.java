package br.com.fsj.salesplatform.adapters.out.sse.mapper;

import br.com.fsj.salesplatform.adapters.out.sse.dto.SseCartItemDiscountDTO;
import br.com.fsj.salesplatform.adapters.out.sse.dto.SseCartItemResponse;
import br.com.fsj.salesplatform.adapters.out.sse.dto.SseCartResponse;
import br.com.fsj.salesplatform.application.core.domain.Cart;
import br.com.fsj.salesplatform.application.core.domain.CartItem;
import br.com.fsj.salesplatform.application.core.domain.CartItemDiscount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SseCartMapper {

    SseCartResponse toResponse(Cart domain);
    
    @Mapping(target = "cartId", ignore = true) 
    SseCartItemResponse toItemResponse(CartItem domain);
    
    @Mapping(target = "cartItemId", ignore = true)
    SseCartItemDiscountDTO toDiscountDTO(CartItemDiscount domain);
}
