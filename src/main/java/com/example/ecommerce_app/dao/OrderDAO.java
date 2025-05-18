package com.example.ecommerce_app.dao;

import com.example.ecommerce_app.model.DeliveryPerson;
import com.example.ecommerce_app.model.Order;
import com.example.ecommerce_app.model.OrderItem;
import com.example.ecommerce_app.model.OrderStatus;
import com.example.ecommerce_app.util.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class OrderDAO {


    public void saveOrder(Order order) throws SQLException {
        // Calculate total price
        double totalPrice = 0;
        for (OrderItem item : order.getItems()) {
            totalPrice += item.getProductPrice() * item.getQuantity();
        }
        order.setTotalPrice(totalPrice);  // Optional: update the order object

        String orderSql = "INSERT INTO orders (full_name, phone, address, comment, payment_method, order_date, status, total_price) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

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
            orderStmt.setDouble(8, totalPrice);
            int rowsAffected = orderStmt.executeUpdate();

            // Retrieve generated order ID
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = orderStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long orderId = generatedKeys.getLong(1);

                        // Save each item
                        for (OrderItem item : order.getItems()) {
                            itemStmt.setLong(1, orderId);
                            itemStmt.setLong(2, item.getProductId());
                            itemStmt.setInt(3, item.getQuantity());
                            itemStmt.addBatch();
                        }

                        itemStmt.executeBatch();

                        // Update product stock
                        String updateStockSql = "UPDATE products SET quantity = quantity - ? WHERE id = ?";
                        try (PreparedStatement updateStockStmt = conn.prepareStatement(updateStockSql)) {
                            for (OrderItem item : order.getItems()) {
                                updateStockStmt.setInt(1, item.getQuantity());
                                updateStockStmt.setLong(2, item.getProductId());
                                updateStockStmt.addBatch();
                            }
                            updateStockStmt.executeBatch();
                        }
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
                order.setTotalPrice(rs.getDouble("total_price"));
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
        order.setTotalPrice(rs.getDouble("total_price"));
        order.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());

        String statusStr = rs.getString("status");
        if (statusStr != null) {
            order.setStatus(OrderStatus.valueOf(statusStr));
        }

        return order;
    }

    private static List<OrderItem> getOrderItemsByOrderId(long orderId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT * FROM order_items WHERE order_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, orderId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                OrderItem item = new OrderItem();
                item.setProductId(rs.getLong("product_id"));
                item.setQuantity(rs.getInt("quantity"));
                System.out.print(item.toString());
                items.add(item);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    public Map<String, Integer> getOrderStatusCounts() {
        Map<String, Integer> result = new HashMap<>();
        String sql = "SELECT status, COUNT(*) AS count FROM orders GROUP BY status";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.put(rs.getString("status"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public Map<String, Integer> getMonthlySales() {
        Map<String, Integer> result = new LinkedHashMap<>();
        String sql = "SELECT MONTH(order_date) AS month, SUM(total_price) AS total FROM orders GROUP BY MONTH(order_date)";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String month = String.valueOf(rs.getInt("month"));
                result.put(month, rs.getInt("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

}