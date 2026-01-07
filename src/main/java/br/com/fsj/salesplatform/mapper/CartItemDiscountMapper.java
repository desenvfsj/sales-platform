package br.com.fsj.salesplatform.mapper;

import br.com.fsj.salesplatform.dto.AddDiscountRequest;
import br.com.fsj.salesplatform.dto.CartItemDiscountDTO;
import br.com.fsj.salesplatform.model.CartItemDiscount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;
import java.util.UUID;

/**
 * Mapper MapStruct para conversão entre entidade CartItemDiscount e DTOs.
 * 
 * <p>MapStruct gera a implementação em tempo de compilação, proporcionando
 * melhor performance que mapeamento manual.</p>
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CartItemDiscountMapper {

    /**
     * Converte entidade CartItemDiscount para DTO.
     * 
     * @param discount entidade CartItemDiscount
     * @return DTO CartItemDiscountDTO
     */
    @Mapping(target = "cartItemId", source = "cartItem.id")
    CartItemDiscountDTO toDTO(CartItemDiscount discount);

    /**
     * Converte lista de entidades para lista de DTOs.
     * 
     * @param discounts lista de entidades
     * @return lista de DTOs
     */
    List<CartItemDiscountDTO> toDTOList(List<CartItemDiscount> discounts);

    /**
     * Converte AddDiscountRequest para entidade CartItemDiscount.
     * 
     * <p>Mapeia apenas os campos do request. Os campos baseAmount, discountPercentage
     * e cartItem devem ser setados manualmente no service, pois são calculados
     * automaticamente com base no subtotal do item.</p>
     * 
     * @param request request de adição de desconto
     * @return entidade CartItemDiscount (sem cartItem, baseAmount e discountPercentage)
     */
    @Mapping(target = "id", expression = "java(stringToUuid(request.id()))")
    @Mapping(target = "cartItem", ignore = true)
    @Mapping(target = "baseAmount", ignore = true)
    @Mapping(target = "discountPercentage", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    CartItemDiscount toEntity(AddDiscountRequest request);
    
    /**
     * Converte String para UUID.
     * 
     * <p>Método auxiliar usado pelo MapStruct para converter IDs String dos DTOs
     * de request para UUID nas entidades.</p>
     * 
     * @param uuidString String representando o UUID
     * @return UUID, ou null se String for null ou vazia
     */
    default UUID stringToUuid(String uuidString) {
        return (uuidString != null && !uuidString.isEmpty()) ? UUID.fromString(uuidString) : null;
    }
}
