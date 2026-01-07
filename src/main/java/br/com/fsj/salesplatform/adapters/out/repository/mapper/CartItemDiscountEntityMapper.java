package br.com.fsj.salesplatform.adapters.out.repository.mapper;

import br.com.fsj.salesplatform.adapters.out.repository.entity.CartItemDiscountEntity;
import br.com.fsj.salesplatform.application.core.domain.CartItemDiscount;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartItemDiscountEntityMapper {
    CartItemDiscount toDomain(CartItemDiscountEntity entity);
    CartItemDiscountEntity toEntity(CartItemDiscount domain);
}
