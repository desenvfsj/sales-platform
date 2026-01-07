package br.com.fsj.salesplatform.adapters.in.controller.mapper;

import br.com.fsj.salesplatform.adapters.in.controller.request.AddDiscountRequest;
import br.com.fsj.salesplatform.adapters.in.controller.request.UpdateDiscountRequest;
import br.com.fsj.salesplatform.adapters.in.controller.response.CartItemDiscountDTO;
import br.com.fsj.salesplatform.application.core.domain.CartItemDiscount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartItemDiscountMapper {
    
    @Mapping(target = "baseAmount", ignore = true)
    @Mapping(target = "discountPercentage", ignore = true)
    CartItemDiscount toDomain(AddDiscountRequest request);

    @Mapping(target = "baseAmount", ignore = true)
    @Mapping(target = "discountPercentage", ignore = true)
    @Mapping(target = "id", ignore = true) // Update request não tem ID do desconto no body geralmente, mas se tiver ignora
    @Mapping(target = "type", ignore = true)
    CartItemDiscount toDomain(UpdateDiscountRequest request);
    
    CartItemDiscountDTO toDTO(CartItemDiscount domain);
    
    List<CartItemDiscountDTO> toDTOList(List<CartItemDiscount> domainList);
}
