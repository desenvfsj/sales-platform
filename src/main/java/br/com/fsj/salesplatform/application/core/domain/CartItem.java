package br.com.fsj.salesplatform.application.core.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CartItem {

    private UUID id;
    private Long productId;
    private String productName;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal subtotal;
    private BigDecimal totalDiscount = BigDecimal.ZERO;
    private Boolean savedForLater = false;
    private List<CartItemDiscount> discounts = new ArrayList<>();

    public CartItem() {
    }

    public CartItem(UUID id, Long productId, String productName, BigDecimal unitPrice, Integer quantity) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.savedForLater = false;
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

    public void addDiscount(CartItemDiscount discount) {
        discounts.add(discount);
        recalculateTotalDiscount();
    }

    public void removeDiscount(CartItemDiscount discount) {
        discounts.remove(discount);
        recalculateTotalDiscount();
    }

    public void recalculateTotalDiscount() {
        this.totalDiscount = discounts.stream()
                .map(CartItemDiscount::getDiscountAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Validação de negócio: Desconto não pode exceder subtotal
        if (subtotal != null && totalDiscount.compareTo(subtotal) > 0) {
            throw new IllegalStateException("Total de descontos (R$ " + totalDiscount + 
                ") não pode exceder o valor do item (R$ " + subtotal + ")");
        }
    }

    public BigDecimal getFinalPrice() {
        if (subtotal == null) return BigDecimal.ZERO;
        return subtotal.subtract(totalDiscount);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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
}
