package com.example.ecommerce_app.dao;

import com.example.ecommerce_app.util.DatabaseConnection;
import com.example.ecommerce_app.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    // Method to get all products
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products";

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check if the connection is null
            if (conn == null) {
                throw new SQLException("Unable to establish a database connection.");
            }

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    Product p = new Product();
                    p.setId(rs.getInt("id"));
                    p.setName(rs.getString("name"));
                    p.setPrice(rs.getDouble("price"));
                    p.setCategory(rs.getString("category"));
                    p.setQuantity(rs.getInt("quantity"));
                    p.setAvailable(rs.getBoolean("available"));
                    // Retrieve image as byte array
                    byte[] imageBytes = rs.getBytes("image");
                    p.setImage(imageBytes);  // Set the image data to the product
                    products.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    // Save a product into the database
    public void saveProduct(Product product) {
        String query = "INSERT INTO products (name, price, category, available, quantity, image) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, product.getName());
            stmt.setDouble(2, product.getPrice());
            stmt.setString(3, product.getCategory());
            stmt.setBoolean(4, product.isAvailable());
            stmt.setInt(5, product.getQuantity());
            stmt.setBytes(6, product.getImage());  // Save the image as a byte array

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Product saved successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update a product in the database
    public void updateProduct(Product product) {
        String sql = "UPDATE products SET name = ?, price = ?, category = ?, quantity = ?, available = ?, image = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, product.getName());
            stmt.setDouble(2, product.getPrice());
            stmt.setString(3, product.getCategory());
            stmt.setInt(4, product.getQuantity());
            stmt.setBoolean(5, product.isAvailable());
            stmt.setBytes(6, product.getImage());
            stmt.setInt(7, product.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Delete a product from the database
    public void deleteProduct(int productId) {
        String sql = "DELETE FROM products WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
