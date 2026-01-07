package br.com.fsj.salesplatform.adapters.in.controller.mapper;

import br.com.fsj.salesplatform.adapters.in.controller.response.CartItemDiscountDTO;
import br.com.fsj.salesplatform.application.core.domain.CartItemDiscount;
import org.mapstruct.Mapper;

/**
 * Mapper para conversão entre CartItemDiscount (Domain) e CartItemDiscountDTO.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface CartItemDiscountDTOMapper {
    
    /**
     * Converte CartItemDiscount (Domain) para CartItemDiscountDTO.
     */
    CartItemDiscountDTO toDTO(CartItemDiscount discount);
}
