package br.com.fsj.salesplatform.adapters.out.repository.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entidade JPA que representa um item do carrinho no banco de dados.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@Entity
@Table(name = "cart_items")
public class CartItemEntity extends AuditableEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private CartEntity cart;

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
    private List<CartItemDiscountEntity> discounts = new ArrayList<>();

    public CartItemEntity() {
    }

    public CartItemEntity(UUID id, Long productId, String productName, BigDecimal unitPrice, Integer quantity) {
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

    public void calculateSubtotal() {
        if (unitPrice != null && quantity != null) {
            this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

    public void updateQuantity(Integer newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        this.quantity = newQuantity;
        calculateSubtotal();
    }

    public void addDiscount(CartItemDiscountEntity discount) {
        discounts.add(discount);
        discount.setCartItem(this);
    }

    public void removeDiscount(CartItemDiscountEntity discount) {
        discounts.remove(discount);
        discount.setCartItem(null);
    }

    public void recalculateTotalDiscount() {
        this.totalDiscount = discounts.stream()
                .map(CartItemDiscountEntity::getDiscountAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getFinalPrice() {
        return subtotal.subtract(totalDiscount);
    }

    // Getters e Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public CartEntity getCart() {
        return cart;
    }

    public void setCart(CartEntity cart) {
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

    public List<CartItemDiscountEntity> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(List<CartItemDiscountEntity> discounts) {
        this.discounts = discounts;
    }
}
