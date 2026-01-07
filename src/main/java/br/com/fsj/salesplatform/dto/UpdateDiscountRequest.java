package br.com.fsj.salesplatform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * DTO para requisição de atualização de desconto.
 * 
 * <p>
 * Apenas o valor do desconto (discountAmount) pode ser atualizado.
 * Os campos baseAmount e discountPercentage são recalculados automaticamente
 * com base no subtotal atual do item.
 * </p>
 */
@Schema(description = "Requisição para atualizar valor de um desconto")
public record UpdateDiscountRequest(

        @NotNull(message = "Valor do desconto é obrigatório") @DecimalMin(value = "0.01", message = "Valor do desconto deve ser maior que zero") @Schema(description = "Novo valor absoluto do desconto em reais", example = "20.00", requiredMode = Schema.RequiredMode.REQUIRED) BigDecimal discountAmount,

        @Schema(description = "Usuário que está atualizando o desconto", example = "admin") String updatedBy) {
}
