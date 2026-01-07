package br.com.fsj.salesplatform.adapters.out.repository.mapper;

import br.com.fsj.salesplatform.adapters.out.repository.entity.CartItemDiscountEntity;
import br.com.fsj.salesplatform.application.core.domain.CartItemDiscount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper para conversão entre CartItemDiscountEntity e CartItemDiscount (Domain).
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface CartItemDiscountEntityMapper {
    
    /**
     * Converte CartItemDiscountEntity para CartItemDiscount (Domain).
     */
    @Mapping(target = "id", expression = "java(entity.getId() != null ? entity.getId().toString() : null)")
    @Mapping(target = "cartItemId", expression = "java(entity.getCartItem() != null && entity.getCartItem().getId() != null ? entity.getCartItem().getId().toString() : null)")
    CartItemDiscount toDomain(CartItemDiscountEntity entity);
    
    /**
     * Converte CartItemDiscount (Domain) para CartItemDiscountEntity.
     */
    @Mapping(target = "id", expression = "java(domain.id() != null ? java.util.UUID.fromString(domain.id()) : null)")
    @Mapping(target = "cartItem", ignore = true)
    CartItemDiscountEntity toEntity(CartItemDiscount domain);
}
