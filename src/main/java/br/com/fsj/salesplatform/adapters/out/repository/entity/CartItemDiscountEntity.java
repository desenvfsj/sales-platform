package br.com.fsj.salesplatform.adapters.out.repository.entity;

import br.com.fsj.salesplatform.application.core.domain.DiscountType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Entidade JPA que representa um desconto aplicado a um item do carrinho no banco de dados.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@Entity
@Table(name = "cart_item_discounts")
public class CartItemDiscountEntity extends AuditableEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_item_id", nullable = false)
    private CartItemEntity cartItem;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private DiscountType type;

    @Column(name = "base_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal baseAmount = BigDecimal.ZERO;

    @Column(name = "discount_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal discountPercentage = BigDecimal.ZERO;

    @Column(name = "discount_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    public CartItemDiscountEntity() {
    }

    public CartItemDiscountEntity(UUID id, CartItemEntity cartItem, DiscountType type,
            BigDecimal baseAmount, BigDecimal discountPercentage, BigDecimal discountAmount) {
        this.id = id;
        this.cartItem = cartItem;
        this.type = type;
        this.baseAmount = baseAmount;
        this.discountPercentage = discountPercentage;
        this.discountAmount = discountAmount;
    }

    public CartItemDiscountEntity(UUID id, CartItemEntity cartItem, DiscountType type,
            BigDecimal baseAmount, BigDecimal discountPercentage) {
        this.id = id;
        this.cartItem = cartItem;
        this.type = type;
        this.baseAmount = baseAmount;
        this.discountPercentage = discountPercentage;
        calculateDiscountAmount();
    }

    public void calculateDiscountAmount() {
        if (baseAmount != null && discountPercentage != null) {
            this.discountAmount = baseAmount
                    .multiply(discountPercentage)
                    .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
        }
    }

    // Getters e Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public CartItemEntity getCartItem() {
        return cartItem;
    }

    public void setCartItem(CartItemEntity cartItem) {
        this.cartItem = cartItem;
    }

    public DiscountType getType() {
        return type;
    }

    public void setType(DiscountType type) {
        this.type = type;
    }

    public BigDecimal getBaseAmount() {
        return baseAmount;
    }

    public void setBaseAmount(BigDecimal baseAmount) {
        this.baseAmount = baseAmount;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }
}
