package br.com.fsj.salesplatform.mapper;

import br.com.fsj.salesplatform.dto.ProductResponse;
import br.com.fsj.salesplatform.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * Mapper para conversão entre Product e ProductResponse usando MapStruct.
 * 
 * MapStruct gera a implementação em tempo de compilação,
 * proporcionando melhor performance que reflexão (MapStruct).
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {

    /**
     * Converte Product para ProductResponse.
     * 
     * @param product entidade de produto
     * @return DTO de resposta
     */
    ProductResponse toProductResponse(Product product);
}

