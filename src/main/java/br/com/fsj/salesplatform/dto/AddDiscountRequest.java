package br.com.fsj.salesplatform.dto;

import br.com.fsj.salesplatform.model.DiscountType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

/**
 * DTO para requisição de adição de desconto a um item do carrinho.
 * 
 * <p>
 * O desconto é informado apenas pelo valor absoluto (discountAmount).
 * Os campos baseAmount e discountPercentage são calculados automaticamente
 * no momento da persistência com base no subtotal do item.
 * </p>
 * 
 * <p>
 * <strong>Campos calculados automaticamente:</strong>
 * </p>
 * <ul>
 * <li>baseAmount: obtido do subtotal do item (quantidade * preço unitário)</li>
 * <li>discountPercentage: calculado como (discountAmount / baseAmount) *
 * 100</li>
 * </ul>
 */
@Schema(description = "Requisição para adicionar desconto a um item do carrinho")
public record AddDiscountRequest(

        @NotBlank(message = "ID do desconto é obrigatório") @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "ID deve ser um UUID válido") @Schema(description = "UUID do desconto", example = "550e8400-e29b-41d4-a716-446655440000", requiredMode = Schema.RequiredMode.REQUIRED) String id,

        @NotNull(message = "Tipo do desconto é obrigatório") @Schema(description = "Tipo do desconto", example = "PROMOCAO", requiredMode = Schema.RequiredMode.REQUIRED) DiscountType type,

        @NotNull(message = "Valor do desconto é obrigatório") @DecimalMin(value = "0.01", message = "Valor do desconto deve ser maior que zero") @Schema(description = "Valor absoluto do desconto em reais", example = "15.50", requiredMode = Schema.RequiredMode.REQUIRED) BigDecimal discountAmount,

        @Schema(description = "Usuário que está aplicando o desconto", example = "admin") String createdBy) {
}
