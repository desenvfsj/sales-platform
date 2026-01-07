package br.com.fsj.salesplatform.mapper;

import br.com.fsj.salesplatform.dto.CartItemResponse;
import br.com.fsj.salesplatform.dto.CartResponse;
import br.com.fsj.salesplatform.dto.CartSummaryResponse;
import br.com.fsj.salesplatform.model.Cart;
import br.com.fsj.salesplatform.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Mapper MapStruct para conversão entre entidades Cart/CartItem e seus DTOs.
 * 
 * <p>MapStruct gera a implementação em tempo de compilação, proporcionando
 * melhor performance que reflexão ou mapeamento manual.</p>
 * 
 * <p><strong>Configuração:</strong></p>
 * <ul>
 *   <li>componentModel = SPRING: registra como bean Spring</li>
 *   <li>uses: injeta outros mappers necessários</li>
 * </ul>
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = {CartItemDiscountMapper.class}
)
public interface CartMapper {

    /**
     * Converte Cart para CartResponse.
     * 
     * <p>Mapeia todos os campos incluindo a lista de itens.</p>
     * 
     * @param cart entidade de carrinho
     * @return DTO de resposta do carrinho
     */
    @Mapping(target = "items", source = "items")
    CartResponse toCartResponse(Cart cart);

    /**
     * Converte Cart para CartSummaryResponse.
     * 
     * <p>Retorna apenas informações resumidas sem os itens.</p>
     * 
     * @param cart entidade de carrinho
     * @return DTO de resumo do carrinho
     */
    CartSummaryResponse toCartSummaryResponse(Cart cart);

    /**
     * Converte CartItem para CartItemResponse incluindo descontos.
     * 
     * <p>Mapeia todos os campos do item, incluindo:</p>
     * <ul>
     *   <li>ID do carrinho (cart.id)</li>
     *   <li>Lista de descontos (delegado ao CartItemDiscountMapper)</li>
     *   <li>Preço final calculado (método customizado)</li>
     * </ul>
     * 
     * @param cartItem entidade de item do carrinho
     * @return DTO de resposta do item
     */
    @Mapping(target = "cartId", source = "cart.id")
    @Mapping(target = "discounts", source = "discounts")
    @Mapping(target = "finalPrice", expression = "java(calculateFinalPrice(cartItem))")
    CartItemResponse toCartItemResponse(CartItem cartItem);

    /**
     * Converte lista de CartItem para lista de CartItemResponse.
     * 
     * @param items lista de entidades de itens
     * @return lista de DTOs de resposta
     */
    List<CartItemResponse> toCartItemResponseList(List<CartItem> items);

    /**
     * Calcula o preço final do item (subtotal - descontos).
     * 
     * <p>Este método é usado pelo MapStruct através da expressão Java
     * no mapeamento de finalPrice.</p>
     * 
     * @param cartItem item do carrinho
     * @return preço final após descontos
     */
    default BigDecimal calculateFinalPrice(CartItem cartItem) {
        if (cartItem == null) {
            return BigDecimal.ZERO;
        }
        return cartItem.getFinalPrice();
    }
    
    /**
     * Converte UUID para String.
     * 
     * <p>Usado pelo MapStruct para converter IDs UUID das entidades
     * para String nos DTOs de resposta.</p>
     * 
     * @param uuid UUID a ser convertido
     * @return String representando o UUID, ou null se UUID for null
     */
    default String uuidToString(UUID uuid) {
        return uuid != null ? uuid.toString() : null;
    }
    
    /**
     * Converte String para UUID.
     * 
     * <p>Usado pelo MapStruct para converter IDs String dos DTOs de request
     * para UUID nas entidades.</p>
     * 
     * @param uuidString String representando o UUID
     * @return UUID, ou null se String for null ou vazia
     */
    default UUID stringToUuid(String uuidString) {
        return (uuidString != null && !uuidString.isEmpty()) ? UUID.fromString(uuidString) : null;
    }
}
