package br.com.fsj.salesplatform.adapters.in.controller.mapper;

import br.com.fsj.salesplatform.adapters.in.controller.response.ProductResponse;
import br.com.fsj.salesplatform.application.core.domain.Product;
import org.mapstruct.Mapper;

/**
 * Mapper para conversão entre Product (Domain) e ProductResponse.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface ProductDTOMapper {
    
    /**
     * Converte Product (Domain) para ProductResponse.
     */
    ProductResponse toResponse(Product product);
}
