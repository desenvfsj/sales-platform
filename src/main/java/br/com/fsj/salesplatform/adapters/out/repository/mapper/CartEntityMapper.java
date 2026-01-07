package br.com.fsj.salesplatform.adapters.out.repository.mapper;

import br.com.fsj.salesplatform.adapters.out.repository.entity.CartEntity;
import br.com.fsj.salesplatform.application.core.domain.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CartItemEntityMapper.class})
public interface CartEntityMapper {
    
    Cart toDomain(CartEntity entity);
    
    CartEntity toEntity(Cart domain);
}
