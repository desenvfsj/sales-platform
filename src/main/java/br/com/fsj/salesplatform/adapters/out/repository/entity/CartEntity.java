package br.com.fsj.salesplatform.adapters.out.repository.entity;

import br.com.fsj.salesplatform.application.core.domain.CartChannel;
import br.com.fsj.salesplatform.application.core.domain.CartStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entidade JPA que representa um carrinho de compras no banco de dados.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@Entity
@Table(name = "carts")
public class CartEntity extends AuditableEntity {

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
    private List<CartItemEntity> items = new ArrayList<>();

    public CartEntity() {
    }

    public CartEntity(UUID id, String customerCpf, CartChannel channel) {
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
     */
    public void calculateTotal() {
        this.grossAmount = items.stream()
                .filter(item -> !item.getSavedForLater())
                .map(CartItemEntity::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.netAmount = items.stream()
                .filter(item -> !item.getSavedForLater())
                .map(CartItemEntity::getFinalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.totalItems = items.stream()
                .filter(item -> !item.getSavedForLater())
                .mapToInt(CartItemEntity::getQuantity)
                .sum();
    }

    public void addItem(CartItemEntity item) {
        items.add(item);
        item.setCart(this);
        calculateTotal();
    }

    public void removeItem(CartItemEntity item) {
        items.remove(item);
        item.setCart(null);
        calculateTotal();
    }

    public boolean isOpen() {
        return status == CartStatus.OPEN;
    }

    public void complete() {
        this.status = CartStatus.COMPLETED;
    }

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

    public BigDecimal getTotalDiscount() {
        return grossAmount.subtract(netAmount);
    }

    public Integer getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }

    public List<CartItemEntity> getItems() {
        return items;
    }

    public void setItems(List<CartItemEntity> items) {
        this.items = items;
    }
}
