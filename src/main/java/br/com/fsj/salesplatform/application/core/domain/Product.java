package br.com.fsj.salesplatform.application.core.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public class Product {

    private String id;
    private String nome;
    private String laboratorio;
    private String grupo;
    private String subGrupo;
    private String linha;
    private BigDecimal preco;
    private BigDecimal custo;
    private Integer estoqueLojaAtual;
    private Map<String, Integer> estoqueOutrasLojas;
    private Boolean temPromocao;
    private LocalDate vencimento;
    private String imagemUrl;
    
    // Novos campos
    private String nomePBM;
    private String mensagemDescontoPBM;
    private Boolean controlado;
    private String detalhamentoCorReceita;
    private Boolean antibiotico;
    private String bula;
    private String alcada;
    private LocalDate ultimaEntradaNota;
    private String planoPagamento;
    private Boolean ruptura;
    private Boolean excesso;

    public Product() {
    }

    public Product(String id, String nome, BigDecimal preco) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLaboratorio() {
        return laboratorio;
    }

    public void setLaboratorio(String laboratorio) {
        this.laboratorio = laboratorio;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getSubGrupo() {
        return subGrupo;
    }

    public void setSubGrupo(String subGrupo) {
        this.subGrupo = subGrupo;
    }

    public String getLinha() {
        return linha;
    }

    public void setLinha(String linha) {
        this.linha = linha;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public BigDecimal getCusto() {
        return custo;
    }

    public void setCusto(BigDecimal custo) {
        this.custo = custo;
    }

    public Integer getEstoqueLojaAtual() {
        return estoqueLojaAtual;
    }

    public void setEstoqueLojaAtual(Integer estoqueLojaAtual) {
        this.estoqueLojaAtual = estoqueLojaAtual;
    }

    public Map<String, Integer> getEstoqueOutrasLojas() {
        return estoqueOutrasLojas;
    }

    public void setEstoqueOutrasLojas(Map<String, Integer> estoqueOutrasLojas) {
        this.estoqueOutrasLojas = estoqueOutrasLojas;
    }

    public Boolean getTemPromocao() {
        return temPromocao;
    }

    public void setTemPromocao(Boolean temPromocao) {
        this.temPromocao = temPromocao;
    }

    public LocalDate getVencimento() {
        return vencimento;
    }

    public void setVencimento(LocalDate vencimento) {
        this.vencimento = vencimento;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }

    public String getNomePBM() {
        return nomePBM;
    }

    public void setNomePBM(String nomePBM) {
        this.nomePBM = nomePBM;
    }

    public String getMensagemDescontoPBM() {
        return mensagemDescontoPBM;
    }

    public void setMensagemDescontoPBM(String mensagemDescontoPBM) {
        this.mensagemDescontoPBM = mensagemDescontoPBM;
    }

    public Boolean getControlado() {
        return controlado;
    }

    public void setControlado(Boolean controlado) {
        this.controlado = controlado;
    }

    public String getDetalhamentoCorReceita() {
        return detalhamentoCorReceita;
    }

    public void setDetalhamentoCorReceita(String detalhamentoCorReceita) {
        this.detalhamentoCorReceita = detalhamentoCorReceita;
    }

    public Boolean getAntibiotico() {
        return antibiotico;
    }

    public void setAntibiotico(Boolean antibiotico) {
        this.antibiotico = antibiotico;
    }

    public String getBula() {
        return bula;
    }

    public void setBula(String bula) {
        this.bula = bula;
    }

    public String getAlcada() {
        return alcada;
    }

    public void setAlcada(String alcada) {
        this.alcada = alcada;
    }

    public LocalDate getUltimaEntradaNota() {
        return ultimaEntradaNota;
    }

    public void setUltimaEntradaNota(LocalDate ultimaEntradaNota) {
        this.ultimaEntradaNota = ultimaEntradaNota;
    }

    public String getPlanoPagamento() {
        return planoPagamento;
    }

    public void setPlanoPagamento(String planoPagamento) {
        this.planoPagamento = planoPagamento;
    }

    public Boolean getRuptura() {
        return ruptura;
    }

    public void setRuptura(Boolean ruptura) {
        this.ruptura = ruptura;
    }

    public Boolean getExcesso() {
        return excesso;
    }

    public void setExcesso(Boolean excesso) {
        this.excesso = excesso;
    }
}
