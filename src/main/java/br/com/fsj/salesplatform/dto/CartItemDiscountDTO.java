package br.com.fsj.salesplatform.dto;

import br.com.fsj.salesplatform.model.DiscountType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para representar um desconto aplicado a um item do carrinho.
 */
@Schema(description = "Desconto aplicado a um item do carrinho")
public record CartItemDiscountDTO(

                @Schema(description = "UUID do desconto", example = "550e8400-e29b-41d4-a716-446655440000") String id,

                @Schema(description = "UUID do item do carrinho", example = "660e8400-e29b-41d4-a716-446655440000") String cartItemId,

                @Schema(description = "Tipo do desconto", example = "PROMOCAO") DiscountType type,

                @Schema(description = "Valor base do produto (quantidade * valor unitário)", example = "100.00") BigDecimal baseAmount,

                @Schema(description = "Percentual de desconto aplicado (0-100)", example = "15.50") BigDecimal discountPercentage,

                @Schema(description = "Valor absoluto do desconto em reais", example = "15.50") BigDecimal discountAmount,

                @Schema(description = "Data/hora de criação do desconto") LocalDateTime createdAt,

                @Schema(description = "Usuário que aplicou o desconto", example = "admin") String createdBy,

                @Schema(description = "Data/hora da última atualização") LocalDateTime updatedAt,

                @Schema(description = "Usuário que fez a última atualização", example = "admin") String updatedBy) {
}
