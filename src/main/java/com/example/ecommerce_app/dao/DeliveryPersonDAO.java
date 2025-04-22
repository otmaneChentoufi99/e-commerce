package com.example.ecommerce_app.dao;

import com.example.ecommerce_app.model.DeliveryPerson;
import com.example.ecommerce_app.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DeliveryPersonDAO {

    public static List<DeliveryPerson> getAllDeliveries() {
        List<DeliveryPerson> deliveries = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'delivery'";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                DeliveryPerson delivery = new DeliveryPerson(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("area"),
                        rs.getString("phone")
                );
                deliveries.add(delivery);
            }

        }catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return deliveries;
    }

    public static void addDelivery(DeliveryPerson delivery) {
        String sql = "INSERT INTO users (username, password, role, area, phone) VALUES (?, ?, 'delivery', ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, delivery.getUsername());
            stmt.setString(2, delivery.getPassword()); // You'll need a field for password in Delivery if needed
            stmt.setString(3, delivery.getArea());
            stmt.setString(4, delivery.getPhone());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteDelivery(int id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateDelivery(DeliveryPerson delivery) {
        String sql = "UPDATE users SET username = ?, area = ?, phone = ? WHERE id = ? AND role = 'delivery'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, delivery.getUsername());
            stmt.setString(2, delivery.getArea());
            stmt.setString(3, delivery.getPhone());
            stmt.setInt(4, delivery.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
