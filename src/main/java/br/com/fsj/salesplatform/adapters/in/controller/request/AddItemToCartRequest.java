package br.com.fsj.salesplatform.adapters.in.controller.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

import jakarta.validation.constraints.Pattern;

/**
 * DTO para requisição de adição de item ao carrinho.
 * 
 * @param id          UUID do item (gerado externamente)
 * @param productId   ID do produto
 * @param productName Nome do produto (snapshot)
 * @param unitPrice   Preço unitário do produto
 * @param quantity    Quantidade desejada
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
public record AddItemToCartRequest(
                @NotBlank(message = "ID do item é obrigatório") @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "ID deve ser um UUID válido") String id,

                @NotNull(message = "ID do produto é obrigatório") Long productId,

                @NotBlank(message = "Nome do produto é obrigatório") String productName,

                @NotNull(message = "Preço do produto é obrigatório") @DecimalMin(value = "0.0", inclusive = true, message = "Preço deve ser maior ou igual a zero") BigDecimal unitPrice,

                @NotNull(message = "Quantidade é obrigatória") @Min(value = 1, message = "Quantidade deve ser maior que zero") Integer quantity) {
}
