package br.com.fsj.salesplatform.application.core.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * Domain object que representa um Produto.
 * 
 * <p>Objeto de domínio puro, sem dependências de frameworks.</p>
 * 
 * <p>Nota: Esta entidade não é persistida em banco de dados,
 * os dados são carregados de um arquivo JSON mockado.</p>
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
public record Product(
    String id,
    String nome,
    String laboratorio,
    String grupo,
    String subGrupo,
    String linha,
    BigDecimal preco,
    BigDecimal custo,
    Integer estoqueLojaAtual,
    Map<String, Integer> estoqueOutrasLojas,
    Boolean temPromocao,
    LocalDate vencimento,
    String imagemUrl,
    String nomePBM,
    String mensagemDescontoPBM,
    Boolean controlado,
    String detalhamentoCorReceita,
    Boolean antibiotico,
    String bula,
    String alcada,
    LocalDate ultimaEntradaNota,
    String planoPagamento,
    Boolean ruptura,
    Boolean excesso
) {
    
    /**
     * Construtor compacto com validações.
     */
    public Product {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID do produto é obrigatório");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do produto é obrigatório");
        }
    }
}
