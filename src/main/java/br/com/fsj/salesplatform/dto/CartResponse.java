package br.com.fsj.salesplatform.dto;

import br.com.fsj.salesplatform.model.CartChannel;
import br.com.fsj.salesplatform.model.CartStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para resposta completa de carrinho com seus itens.
 * 
 * @param id            UUID do carrinho (String)
 * @param customerCpf   CPF do cliente
 * @param channel       Canal de origem
 * @param status        Status do carrinho
 * @param grossAmount   Valor bruto total (soma dos subtotais sem descontos)
 * @param netAmount     Valor líquido total (valor bruto - descontos)
 * @param totalDiscount Total de descontos aplicados
 * @param totalItems    Quantidade total de itens
 * @param items         Lista de itens do carrinho
 * @param createdAt     Data de criação
 * @param updatedAt     Data da última atualização
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
public record CartResponse(
                String id,
                String customerCpf,
                CartChannel channel,
                CartStatus status,
                BigDecimal grossAmount,
                BigDecimal netAmount,
                BigDecimal totalDiscount,
                Integer totalItems,
                List<CartItemResponse> items,
                LocalDateTime createdAt,
                LocalDateTime updatedAt) {
}
