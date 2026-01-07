package br.com.fsj.salesplatform.adapters.out.repository.mapper;

import br.com.fsj.salesplatform.adapters.out.repository.entity.CartItemEntity;
import br.com.fsj.salesplatform.application.core.domain.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CartItemDiscountEntityMapper.class})
public interface CartItemEntityMapper {
    
    @Mapping(target = "savedForLater", defaultValue = "false")
    CartItem toDomain(CartItemEntity entity);
    
    CartItemEntity toEntity(CartItem domain);
}
