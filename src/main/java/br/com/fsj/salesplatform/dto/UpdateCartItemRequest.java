package br.com.fsj.salesplatform.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para requisição de atualização de item do carrinho.
 * 
 * @param quantity Nova quantidade do item
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
public record UpdateCartItemRequest(
        @NotNull(message = "Quantidade é obrigatória")
        @Min(value = 1, message = "Quantidade deve ser maior que zero")
        Integer quantity
) {
}

