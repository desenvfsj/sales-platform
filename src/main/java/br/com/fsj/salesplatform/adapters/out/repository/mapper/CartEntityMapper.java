package br.com.fsj.salesplatform.adapters.out.repository.mapper;

import br.com.fsj.salesplatform.adapters.out.repository.entity.CartEntity;
import br.com.fsj.salesplatform.application.core.domain.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper para conversão entre CartEntity e Cart (Domain).
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@Mapper(componentModel = "spring", uses = {CartItemEntityMapper.class})
public interface CartEntityMapper {
    
    /**
     * Converte CartEntity para Cart (Domain).
     */
    @Mapping(target = "id", expression = "java(entity.getId() != null ? entity.getId().toString() : null)")
    Cart toDomain(CartEntity entity);
    
    /**
     * Converte Cart (Domain) para CartEntity.
     */
    @Mapping(target = "id", expression = "java(domain.id() != null ? java.util.UUID.fromString(domain.id()) : null)")
    CartEntity toEntity(Cart domain);
}
