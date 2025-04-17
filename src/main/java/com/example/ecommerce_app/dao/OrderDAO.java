package com.example.ecommerce_app.dao;

import com.example.ecommerce_app.model.Order;
import com.example.ecommerce_app.model.OrderItem;
import com.example.ecommerce_app.util.DatabaseConnection;

import java.sql.*;

public class OrderDAO {


    public void saveOrder(Order order) throws SQLException {
        String orderSql = "INSERT INTO orders (full_name, phone, address, comment, payment_method, order_date) VALUES (?, ?, ?, ?, ?, ?)";

        // Use a single PreparedStatement to handle both the order and items
        String itemSql = "INSERT INTO order_items (order_id, product_id, quantity) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement orderStmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);  // Specify to return generated keys
             PreparedStatement itemStmt = conn.prepareStatement(itemSql)
        ) {
            // Save order
            orderStmt.setString(1, order.getFullName());
            orderStmt.setString(2, order.getPhone());
            orderStmt.setString(3, order.getAddress());
            orderStmt.setString(4, order.getComment());
            orderStmt.setString(5, order.getPaymentMethod());
            orderStmt.setTimestamp(6, java.sql.Timestamp.valueOf(order.getOrderDate()));

            int rowsAffected = orderStmt.executeUpdate();

            // Retrieve generated order ID
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = orderStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long orderId = generatedKeys.getLong(1);

                        // Save each item
                        for (OrderItem item : order.getItems()) {
                            itemStmt.setLong(1, orderId);  // Use the generated order ID here
                            itemStmt.setLong(2, item.getProductId());
                            itemStmt.setInt(3, item.getQuantity());
                            itemStmt.addBatch();
                        }

                        itemStmt.executeBatch();  // Insert all items in a batch
                    } else {
                        throw new SQLException("Failed to retrieve order ID.");
                    }
                }
            }
        }
    }
}