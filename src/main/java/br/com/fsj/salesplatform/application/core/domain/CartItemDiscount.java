package br.com.fsj.salesplatform.application.core.domain;

import java.math.BigDecimal;
import java.util.UUID;

public class CartItemDiscount {

    private UUID id;
    private DiscountType type;
    private BigDecimal baseAmount = BigDecimal.ZERO;
    private BigDecimal discountPercentage = BigDecimal.ZERO;
    private BigDecimal discountAmount = BigDecimal.ZERO;

    public CartItemDiscount() {
    }

    public CartItemDiscount(UUID id, DiscountType type,
            BigDecimal baseAmount, BigDecimal discountPercentage, BigDecimal discountAmount) {
        this.id = id;
        this.type = type;
        this.baseAmount = baseAmount;
        this.discountPercentage = discountPercentage;
        this.discountAmount = discountAmount;
    }

    public CartItemDiscount(UUID id, DiscountType type,
            BigDecimal baseAmount, BigDecimal discountPercentage) {
        this.id = id;
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

    public void calculateDiscountPercentage() {
        if (baseAmount != null && baseAmount.compareTo(BigDecimal.ZERO) > 0 && discountAmount != null) {
            this.discountPercentage = discountAmount
                    .divide(baseAmount, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, java.math.RoundingMode.HALF_UP);
        } else {
            this.discountPercentage = BigDecimal.ZERO;
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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
