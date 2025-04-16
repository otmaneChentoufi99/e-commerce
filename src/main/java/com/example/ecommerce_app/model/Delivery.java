package com.example.ecommerce_app.model;

public class Delivery {
    private int id;
    private int orderId;
    private String deliveryStatus;
    private String deliveryDate;
    private String deliveryAddress;
    private boolean paid;  // New property to track if the order has been paid

    public Delivery(int id, int orderId, String deliveryStatus, String deliveryDate, String deliveryAddress, boolean paid) {
        this.id = id;
        this.orderId = orderId;
        this.deliveryStatus = deliveryStatus;
        this.deliveryDate = deliveryDate;
        this.deliveryAddress = deliveryAddress;
        this.paid = paid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }
}
