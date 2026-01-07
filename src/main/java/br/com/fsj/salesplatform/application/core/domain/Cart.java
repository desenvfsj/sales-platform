package br.com.fsj.salesplatform.application.core.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Cart {

    private UUID id;
    private String customerCpf;
    private CartChannel channel;
    private CartStatus status = CartStatus.OPEN;
    private BigDecimal grossAmount = BigDecimal.ZERO;
    private BigDecimal netAmount = BigDecimal.ZERO;
    private Integer totalItems = 0;
    private List<CartItem> items = new ArrayList<>();
    
    // Campos de auditoria úteis no domínio
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Cart() {
    }

    public Cart(UUID id, String customerCpf, CartChannel channel) {
        this.id = id;
        this.customerCpf = customerCpf;
        this.channel = channel;
        this.status = CartStatus.OPEN;
        this.grossAmount = BigDecimal.ZERO;
        this.netAmount = BigDecimal.ZERO;
        this.totalItems = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

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
                
        this.updatedAt = LocalDateTime.now();
    }

    public void addItem(CartItem item) {
        // Verifica se item já existe pelo productId
        var existingItem = items.stream()
                .filter(i -> i.getProductId().equals(item.getProductId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem existing = existingItem.get();
            existing.updateQuantity(existing.getQuantity() + item.getQuantity());
            // Atualizar ID se necessário? Geralmente mantemos o ID do item existente
        } else {
            items.add(item);
        }
        calculateTotal();
    }

    public void updateItemQuantity(UUID itemId, Integer newQuantity) {
        var item = items.stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item não encontrado no carrinho"));
        
        item.updateQuantity(newQuantity);
        calculateTotal();
    }

    public void removeItem(UUID itemId) {
        items.removeIf(i -> i.getId().equals(itemId));
        calculateTotal();
    }

    public void setItemSavedForLater(UUID itemId, boolean saved) {
        var item = items.stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item não encontrado no carrinho"));
        
        item.setSavedForLater(saved);
        calculateTotal();
    }

    public void removeItem(CartItem item) {
        items.remove(item);
        calculateTotal();
    }

    public boolean isOpen() {
        return status == CartStatus.OPEN;
    }

    public void complete() {
        this.status = CartStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel() {
        this.status = CartStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    public BigDecimal getTotalDiscount() {
        return grossAmount.subtract(netAmount);
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
