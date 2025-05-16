package com.example.ecommerce_app.model;

public class OrderItem {
    private long productId;
    private String productName;
    private int quantity;
    private double productPrice;

    // Constructors
    public OrderItem() {}

    public OrderItem(long productId, String productName, int quantity, double productPrice) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.productPrice = productPrice;
    }

    public OrderItem(int id, int qty) {
        this.productId = id;
        this.quantity = qty;
    }

    // Getters and Setters
    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    // Optional: Calculate total price for this item
    public double getTotalPrice() {
        return productPrice * quantity;
    }
}
