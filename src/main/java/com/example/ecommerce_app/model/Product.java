package com.example.ecommerce_app.model;

public class Product {
    private int id;
    private String name;
    private double price;
    private String category;
    private int quantity;
    private boolean available;
    private byte[] image;  // Store the image as byte array


    public Product() {
        // Default constructor needed for TableView
    }

    public Product(String name,
                   double price,
                   String category,
                   boolean available,
                   int quantity,
                   byte[] image) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.quantity = quantity;
        this.available = available;
        this.image = image;  // Set the image field
    }


    public int getId() {return id;}
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }
    public int getQuantity() { return quantity; }
    public boolean isAvailable() {return available;}
    public byte[] getImage() {return image;}

    public void setId(int id) {
        this.id = id;
    }

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

    public void setImage(byte[] image) {
        this.image = image;
    }
}
