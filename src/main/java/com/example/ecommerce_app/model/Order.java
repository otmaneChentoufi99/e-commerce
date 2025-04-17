package com.example.ecommerce_app.model;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Order {
    private String fullName;
    private String phone;
    private String address;
    private String comment;
    private String paymentMethod;
    private LocalDateTime orderDate = LocalDateTime.now();
    private List<OrderItem> items = new ArrayList<>();

    public Order(String fullName, String phone, String address, String comment, String paymentMethod) {
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.comment = comment;
        this.paymentMethod = paymentMethod;
    }

    public void addItem(OrderItem item) {
        items.add(item);
    }

    public List<OrderItem> getItems() {
        return items;
    }

    // Getters
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getComment() { return comment; }
    public String getPaymentMethod() { return paymentMethod; }
    public LocalDateTime getOrderDate() { return orderDate; }
}
