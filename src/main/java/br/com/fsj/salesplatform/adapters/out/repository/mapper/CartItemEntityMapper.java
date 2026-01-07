package br.com.fsj.salesplatform.adapters.out.repository.mapper;

import br.com.fsj.salesplatform.adapters.out.repository.entity.CartItemEntity;
import br.com.fsj.salesplatform.application.core.domain.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper para conversão entre CartItemEntity e CartItem (Domain).
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@Mapper(componentModel = "spring", uses = {CartItemDiscountEntityMapper.class})
public interface CartItemEntityMapper {
    
    /**
     * Converte CartItemEntity para CartItem (Domain).
     */
    @Mapping(target = "id", expression = "java(entity.getId() != null ? entity.getId().toString() : null)")
    @Mapping(target = "cartId", expression = "java(entity.getCart() != null && entity.getCart().getId() != null ? entity.getCart().getId().toString() : null)")
    CartItem toDomain(CartItemEntity entity);
    
    /**
     * Converte CartItem (Domain) para CartItemEntity.
     */
    @Mapping(target = "id", expression = "java(domain.id() != null ? java.util.UUID.fromString(domain.id()) : null)")
    @Mapping(target = "cart", ignore = true)
    CartItemEntity toEntity(CartItem domain);
}
