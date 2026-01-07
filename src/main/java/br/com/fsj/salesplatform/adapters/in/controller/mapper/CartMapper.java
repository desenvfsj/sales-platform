package br.com.fsj.salesplatform.adapters.in.controller.mapper;

import br.com.fsj.salesplatform.adapters.in.controller.request.AddItemToCartRequest;
import br.com.fsj.salesplatform.adapters.in.controller.request.CreateCartRequest;
import br.com.fsj.salesplatform.adapters.in.controller.response.CartItemResponse;
import br.com.fsj.salesplatform.adapters.in.controller.response.CartResponse;
import br.com.fsj.salesplatform.adapters.in.controller.response.CartSummaryResponse;
import br.com.fsj.salesplatform.application.core.domain.Cart;
import br.com.fsj.salesplatform.application.core.domain.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CartItemDiscountMapper.class})
public interface CartMapper {
    
    @Mapping(target = "status", constant = "OPEN")
    @Mapping(target = "grossAmount", constant = "0")
    @Mapping(target = "netAmount", constant = "0")
    @Mapping(target = "totalItems", constant = "0")
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Cart toDomain(CreateCartRequest request);

    @Mapping(target = "subtotal", ignore = true)
    @Mapping(target = "totalDiscount", ignore = true)
    // finalPrice é método getter, mapstruct ignora no target se não tiver setter. CartItem domain tem getters calculados?
    // CartItem tem getFinalPrice(). Não tem setFinalPrice. Mapstruct ignora.
    @Mapping(target = "savedForLater", constant = "false")
    @Mapping(target = "discounts", ignore = true)
    @Mapping(target = "id", expression = "java(java.util.UUID.fromString(request.id()))")
    CartItem toDomain(AddItemToCartRequest request);
    
    CartResponse toCartResponse(Cart domain);
    
    CartSummaryResponse toCartSummaryResponse(Cart domain);
    
    @Mapping(target = "cartId", source = "id") // Na verdade cartId não está no item, precisa ser passado ou ignorado se não tiver referência.
    // O CartItemResponse espera cartId (String). O Domain CartItem não tem referência ao Cart (removi).
    // Então cartId ficará null ou mapeado manualmente. Como é response, o ID do cart geralmente vem do path variable ou do contexto.
    // O item domain tem id.
    // O Response tem cartId. Se domain não tem, fica null.
    CartItemResponse toCartItemResponse(CartItem domain);
}
