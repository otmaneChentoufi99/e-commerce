package com.example.ecommerce_app.dao;

import com.example.ecommerce_app.model.DeliveryPerson;
import com.example.ecommerce_app.model.Order;
import com.example.ecommerce_app.model.OrderItem;
import com.example.ecommerce_app.model.OrderStatus;
import com.example.ecommerce_app.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {


    public void saveOrder(Order order) throws SQLException {
        String orderSql = "INSERT INTO orders (full_name, phone, address, comment, payment_method, order_date, status) VALUES (?, ?, ?, ?, ?, ?, ?)";

        String itemSql = "INSERT INTO order_items (order_id, product_id, quantity) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement orderStmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement itemStmt = conn.prepareStatement(itemSql)
        ) {
            // Save order
            orderStmt.setString(1, order.getFullName());
            orderStmt.setString(2, order.getPhone());
            orderStmt.setString(3, order.getAddress());
            orderStmt.setString(4, order.getComment());
            orderStmt.setString(5, order.getPaymentMethod());
            orderStmt.setTimestamp(6, java.sql.Timestamp.valueOf(order.getOrderDate()));
            orderStmt.setString(7, order.getStatus().name());
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
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();

        String orderSql = """
        SELECT o.*, 
               u.id as dp_id, 
               u.username as dp_username,
               u.phone as dp_phone,
               u.area as dp_area
        FROM orders o
        LEFT JOIN users u ON o.delivery_person_id = u.id AND u.role = 'DELIVERY'
        """;

        String itemsSql = """
        SELECT oi.product_id, oi.quantity, p.name AS product_name
        FROM order_items oi
        JOIN products p ON oi.product_id = p.id
        WHERE oi.order_id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement orderStmt = conn.prepareStatement(orderSql);
             ResultSet rs = orderStmt.executeQuery()) {

            while (rs.next()) {
                Order order = new Order();
                long orderId = rs.getLong("id");

                // Set basic order info
                order.setId(orderId);
                order.setFullName(rs.getString("full_name"));
                order.setPhone(rs.getString("phone"));  // Customer phone
                order.setAddress(rs.getString("address"));
                order.setComment(rs.getString("comment"));
                order.setStatus(OrderStatus.valueOf(rs.getString("status")));
                order.setPaymentMethod(rs.getString("payment_method"));
                order.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());

                // Set delivery person info
                int dpId = rs.getInt("dp_id");
                if (!rs.wasNull()) {
                    DeliveryPerson dp = new DeliveryPerson();
                    dp.setId(dpId);
                    dp.setUsername(rs.getString("dp_username"));
                    dp.setPhone(rs.getString("dp_phone"));  // Delivery person's phone
                    dp.setArea(rs.getString("dp_area"));
                    order.setDeliveryPerson(dp);
                }

                // Get order items
                try (PreparedStatement itemsStmt = conn.prepareStatement(itemsSql)) {
                    itemsStmt.setLong(1, orderId);
                    try (ResultSet itemRs = itemsStmt.executeQuery()) {
                        List<OrderItem> items = new ArrayList<>();
                        while (itemRs.next()) {
                            OrderItem item = new OrderItem();
                            item.setProductId(itemRs.getLong("product_id"));
                            item.setQuantity(itemRs.getInt("quantity"));
                            item.setProductName(itemRs.getString("product_name"));
                            items.add(item);
                        }
                        order.setItems(items);
                    }
                }
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Consider proper error handling
        }
        return orders;
    }

    public void assignDeliveryPerson(Long orderId, int deliveryPersonId) {
        String sql = "UPDATE orders SET delivery_person_id = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, deliveryPersonId);
            stmt.setLong(2, orderId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Add this method to OrderDAO:
    public void updateOrderStatus(Long orderId, OrderStatus status) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setLong(2, orderId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Order> getOrdersByDeliveryPerson(int deliveryPersonId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE delivery_person_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, deliveryPersonId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                orders.add(order);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }

    private static Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getLong("id"));
        order.setFullName(rs.getString("full_name"));
        order.setPhone(rs.getString("phone"));
        order.setAddress(rs.getString("address"));
        order.setPaymentMethod(rs.getString("payment_method"));
        order.setComment(rs.getString("comment"));
        order.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());

        String statusStr = rs.getString("status");
        if (statusStr != null) {
            order.setStatus(OrderStatus.valueOf(statusStr));
        }

        return order;
    }


}