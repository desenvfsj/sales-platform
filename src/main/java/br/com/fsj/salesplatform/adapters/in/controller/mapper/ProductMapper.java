package br.com.fsj.salesplatform.adapters.in.controller.mapper;

import br.com.fsj.salesplatform.adapters.in.controller.response.ProductResponse;
import br.com.fsj.salesplatform.application.core.domain.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductResponse toProductResponse(Product domain);
}
