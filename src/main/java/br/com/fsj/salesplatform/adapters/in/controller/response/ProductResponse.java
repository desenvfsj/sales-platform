package br.com.fsj.salesplatform.adapters.in.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Schema(description = "Dados de um produto farmacêutico")
public record ProductResponse(
        @Schema(description = "Identificador único do produto", example = "1")
        String id,
        
        @Schema(description = "Nome do produto", example = "Dipirona Sódica 500mg")
        String nome,
        
        @Schema(description = "Laboratório fabricante", example = "Medley")
        String laboratorio,
        
        @Schema(description = "Grupo do produto", example = "Analgésicos")
        String grupo,
        
        @Schema(description = "Subgrupo do produto", example = "Antitérmicos")
        String subGrupo,
        
        @Schema(description = "Linha do produto", example = "Genérico")
        String linha,
        
        @Schema(description = "Preço de venda", example = "8.50")
        BigDecimal preco,
        
        @Schema(description = "Custo do produto", example = "5.20")
        BigDecimal custo,
        
        @Schema(description = "Quantidade em estoque na loja atual", example = "45")
        Integer estoqueLojaAtual,
        
        @Schema(description = "Estoque em outras lojas", example = "{\"Loja 2\": 30, \"Loja 3\": 25}")
        Map<String, Integer> estoqueOutrasLojas,
        
        @Schema(description = "Indica se o produto está em promoção", example = "true")
        Boolean temPromocao,
        
        @Schema(description = "Data de vencimento do produto", example = "2025-12-31")
        LocalDate vencimento,
        
        @Schema(description = "URL da imagem do produto", example = "https://dummyimage.com/300x300/4A90E2/ffffff&text=Dipirona+500mg")
        String imagemUrl,
        
        @Schema(description = "Nome do produto no PBM (Programa de Benefício de Medicamentos)", example = "Dipirona Sódica Genérico")
        String nomePBM,
        
        @Schema(description = "Mensagem de desconto do PBM", example = "Desconto de 40% com PBM")
        String mensagemDescontoPBM,
        
        @Schema(description = "Indica se o produto é controlado", example = "false")
        Boolean controlado,
        
        @Schema(description = "Detalhamento da cor da receita necessária", example = "Receita Branca Simples")
        String detalhamentoCorReceita,
        
        @Schema(description = "Indica se o produto é antibiótico", example = "false")
        Boolean antibiotico,
        
        @Schema(description = "URL ou texto da bula do medicamento", example = "https://example.com/bula/dipirona.pdf")
        String bula,
        
        @Schema(description = "Nível de alçada necessário para venda", example = "Gerente")
        String alcada,
        
        @Schema(description = "Data da última entrada do produto em nota fiscal", example = "2025-12-20")
        LocalDate ultimaEntradaNota,
        
        @Schema(description = "Plano de pagamento disponível", example = "À vista, 2x, 3x sem juros")
        String planoPagamento,
        
        @Schema(description = "Indica se o produto está em ruptura de estoque", example = "false")
        Boolean ruptura,
        
        @Schema(description = "Indica se o produto está em excesso de estoque", example = "false")
        Boolean excesso
) {
    public BigDecimal calcularMargemLucro() {
        if (custo == null || custo.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal lucro = preco.subtract(custo);
        return lucro.divide(custo, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
    }
    
    public Integer calcularEstoqueTotal() {
        int total = estoqueLojaAtual != null ? estoqueLojaAtual : 0;
        
        if (estoqueOutrasLojas != null) {
            total += estoqueOutrasLojas.values().stream()
                    .mapToInt(Integer::intValue)
                    .sum();
        }
        
        return total;
    }
}
