package br.com.fsj.salesplatform.adapters.in.controller.mapper;

import br.com.fsj.salesplatform.adapters.in.controller.response.CartItemResponse;
import br.com.fsj.salesplatform.application.core.domain.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper para conversão entre CartItem (Domain) e CartItemResponse.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@Mapper(componentModel = "spring", uses = {CartItemDiscountDTOMapper.class})
public interface CartItemDTOMapper {
    
    /**
     * Converte CartItem (Domain) para CartItemResponse.
     */
    @Mapping(target = "finalPrice", expression = "java(cartItem.getFinalPrice())")
    CartItemResponse toResponse(CartItem cartItem);
}
