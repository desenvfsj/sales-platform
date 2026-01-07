package br.com.fsj.salesplatform.adapters.in.controller.response;

import br.com.fsj.salesplatform.application.core.domain.CartStatus;

import java.math.BigDecimal;

/**
 * DTO para resposta resumida do carrinho.
 * 
 * <p>
 * Utilizado quando não é necessário retornar todos os itens,
 * apenas informações agregadas.
 * </p>
 * 
 * @param id            UUID do carrinho (String)
 * @param customerCpf   CPF do cliente
 * @param status        Status do carrinho
 * @param grossAmount   Valor bruto total (soma dos subtotais sem descontos)
 * @param netAmount     Valor líquido total (valor bruto - descontos)
 * @param totalDiscount Total de descontos aplicados
 * @param totalItems    Quantidade total de itens
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
public record CartSummaryResponse(
                String id,
                String customerCpf,
                CartStatus status,
                BigDecimal grossAmount,
                BigDecimal netAmount,
                BigDecimal totalDiscount,
                Integer totalItems) {
}
