package br.com.fsj.salesplatform.adapters.in.controller.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para resposta de item do carrinho.
 * 
 * @param id            UUID do item (String)
 * @param cartId        UUID do carrinho (String)
 * @param productId     ID do produto
 * @param productName   Nome do produto
 * @param unitPrice     Preço unitário
 * @param quantity      Quantidade
 * @param subtotal      Subtotal calculado (unitPrice * quantity)
 * @param discounts     Lista de descontos aplicados ao item
 * @param totalDiscount Soma de todos os descontos (calculado via código Java)
 * @param finalPrice    Preço final após descontos (subtotal - totalDiscount)
 * @param savedForLater Flag de salvar para depois
 * @param createdAt     Data de adição
 * @param updatedAt     Data da última atualização
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
public record CartItemResponse(
                String id,
                String cartId,
                Long productId,
                String productName,
                BigDecimal unitPrice,
                Integer quantity,
                BigDecimal subtotal,
                List<CartItemDiscountDTO> discounts,
                BigDecimal totalDiscount,
                BigDecimal finalPrice,
                Boolean savedForLater,
                LocalDateTime createdAt,
                LocalDateTime updatedAt) {
}
