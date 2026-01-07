package br.com.fsj.salesplatform.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entidade que representa um carrinho de compras.
 * 
 * <p>
 * Um carrinho pertence a um cliente (identificado por CPF) e pode conter
 * múltiplos itens.
 * Suporta múltiplos canais de venda (WEB, APP, PDV, PARTNER).
 * </p>
 * 
 * <p>
 * <strong>Regras de Negócio:</strong>
 * </p>
 * <ul>
 * <li>Cliente pode ter apenas 1 carrinho ACTIVE por vez por canal</li>
 * <li>CPF deve conter exatamente 11 dígitos numéricos</li>
 * <li>Total e quantidade são calculados automaticamente</li>
 * <li>Carrinho COMPLETED não pode ser modificado</li>
 * </ul>
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@Entity
@Table(name = "carts")
public class Cart extends AuditableEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "customer_cpf", nullable = false, length = 11)
    private String customerCpf;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 20)
    private CartChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CartStatus status = CartStatus.OPEN;

    @Column(name = "gross_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal grossAmount = BigDecimal.ZERO;

    @Column(name = "net_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal netAmount = BigDecimal.ZERO;

    @Column(name = "total_items", nullable = false)
    private Integer totalItems = 0;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CartItem> items = new ArrayList<>();

    /**
     * Construtor padrão (requerido pelo JPA).
     */
    public Cart() {
    }

    /**
     * Construtor para criar novo carrinho.
     */
    public Cart(UUID id, String customerCpf, CartChannel channel) {
        this.id = id;
        this.customerCpf = customerCpf;
        this.channel = channel;
        this.status = CartStatus.OPEN;
        this.grossAmount = BigDecimal.ZERO;
        this.netAmount = BigDecimal.ZERO;
        this.totalItems = 0;
    }

    /**
     * Calcula os totais do carrinho (bruto e líquido) somando os valores dos itens.
     * 
     * <p>
     * Gross Amount: soma dos subtotais dos itens (sem descontos)
     * </p>
     * <p>
     * Net Amount: soma dos preços finais dos itens (com descontos aplicados)
     * </p>
     */
    public void calculateTotal() {
        this.grossAmount = items.stream()
                .filter(item -> !item.getSavedForLater())
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.netAmount = items.stream()
                .filter(item -> !item.getSavedForLater())
                .map(CartItem::getFinalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.totalItems = items.stream()
                .filter(item -> !item.getSavedForLater())
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    /**
     * Adiciona um item ao carrinho.
     */
    public void addItem(CartItem item) {
        items.add(item);
        item.setCart(this);
        calculateTotal();
    }

    /**
     * Remove um item do carrinho.
     */
    public void removeItem(CartItem item) {
        items.remove(item);
        item.setCart(null);
        calculateTotal();
    }

    /**
     * Verifica se o carrinho está aberto e pode ser modificado.
     */
    public boolean isOpen() {
        return status == CartStatus.OPEN;
    }

    /**
     * Finaliza o carrinho (checkout).
     */
    public void complete() {
        this.status = CartStatus.COMPLETED;
    }

    /**
     * Cancela o carrinho.
     */
    public void cancel() {
        this.status = CartStatus.CANCELLED;
    }

    // Getters e Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCustomerCpf() {
        return customerCpf;
    }

    public void setCustomerCpf(String customerCpf) {
        this.customerCpf = customerCpf;
    }

    public CartChannel getChannel() {
        return channel;
    }

    public void setChannel(CartChannel channel) {
        this.channel = channel;
    }

    public CartStatus getStatus() {
        return status;
    }

    public void setStatus(CartStatus status) {
        this.status = status;
    }

    public BigDecimal getGrossAmount() {
        return grossAmount;
    }

    public void setGrossAmount(BigDecimal grossAmount) {
        this.grossAmount = grossAmount;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    /**
     * Retorna o total de descontos aplicados no carrinho.
     * 
     * @return diferença entre valor bruto e valor líquido
     */
    public BigDecimal getTotalDiscount() {
        return grossAmount.subtract(netAmount);
    }

    public Integer getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }
}
