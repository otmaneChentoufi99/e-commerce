package com.example.ecommerce_app.model;

public class Product {
    private String name;
    private double price;
    private String category;
    private int quantity;
    private boolean available;

    public Product() {
        // Default constructor needed for TableView
    }

    public Product(String name,
                   double price,
                   String category,
                   boolean available,
                   int quantity) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.quantity = quantity;
        this.available = available;

    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }
    public int getQuantity() { return quantity; }
    public boolean isAvailable() {return available;}

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
