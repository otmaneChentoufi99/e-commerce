package com.example.ecommerce_app.model;

public class OrderItem {
    private long productId;
    private int quantity;

    public OrderItem(long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public long getProductId() { return productId; }
    public int getQuantity() { return quantity; }
}
