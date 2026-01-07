package br.com.fsj.salesplatform.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entidade que representa um item do carrinho de compras.
 * 
 * <p>
 * Cada item contém um snapshot do produto (nome e preço) no momento
 * da adição ao carrinho, garantindo consistência de preços.
 * </p>
 * 
 * <p>
 * <strong>Regras de Negócio:</strong>
 * </p>
 * <ul>
 * <li>Quantidade deve ser maior que zero</li>
 * <li>Preço unitário deve ser maior ou igual a zero</li>
 * <li>Subtotal é calculado automaticamente (unitPrice * quantity)</li>
 * <li>Total de descontos é recalculado via código Java sempre que descontos são
 * adicionados/alterados/removidos</li>
 * <li>Não permitir produto duplicado no mesmo carrinho</li>
 * </ul>
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@Entity
@Table(name = "cart_items")
public class CartItem extends AuditableEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "total_discount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalDiscount = BigDecimal.ZERO;

    @Column(name = "saved_for_later", nullable = false)
    private Boolean savedForLater = false;

    @OneToMany(mappedBy = "cartItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItemDiscount> discounts = new ArrayList<>();

    /**
     * Construtor padrão (requerido pelo JPA).
     */
    public CartItem() {
    }

    /**
     * Construtor para criar novo item.
     */
    public CartItem(UUID id, Long productId, String productName, BigDecimal unitPrice, Integer quantity) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.savedForLater = false;
        calculateSubtotal();
    }

    @PrePersist
    protected void onCreate() {
        super.onCreate();
        calculateSubtotal();
    }

    @PreUpdate
    protected void onUpdate() {
        super.onUpdate();
        calculateSubtotal();
    }

    /**
     * Calcula o subtotal do item (unitPrice * quantity).
     */
    public void calculateSubtotal() {
        if (unitPrice != null && quantity != null) {
            this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

    /**
     * Atualiza a quantidade do item.
     */
    public void updateQuantity(Integer newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        this.quantity = newQuantity;
        calculateSubtotal();
    }

    // Getters e Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        calculateSubtotal();
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        calculateSubtotal();
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(BigDecimal totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public Boolean getSavedForLater() {
        return savedForLater;
    }

    public void setSavedForLater(Boolean savedForLater) {
        this.savedForLater = savedForLater;
    }

    public List<CartItemDiscount> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(List<CartItemDiscount> discounts) {
        this.discounts = discounts;
    }

    /**
     * Adiciona um desconto ao item.
     */
    public void addDiscount(CartItemDiscount discount) {
        discounts.add(discount);
        discount.setCartItem(this);
    }

    /**
     * Remove um desconto do item.
     */
    public void removeDiscount(CartItemDiscount discount) {
        discounts.remove(discount);
        discount.setCartItem(null);
    }

    /**
     * Recalcula e atualiza o total de descontos a partir da lista de descontos.
     * Este método deve ser chamado sempre que a lista de descontos for modificada.
     */
    public void recalculateTotalDiscount() {
        this.totalDiscount = discounts.stream()
                .map(CartItemDiscount::getDiscountAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calcula o preço final do item (subtotal - descontos).
     */
    public BigDecimal getFinalPrice() {
        return subtotal.subtract(totalDiscount);
    }
}
