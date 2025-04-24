package com.example.ecommerce_app.dao;


import com.example.ecommerce_app.model.User;
import com.example.ecommerce_app.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public static User login(String username, String password) {

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
               User user = new User();
               user.setId(rs.getInt("id"));
               user.setUsername(rs.getString("username"));
               user.setRole(rs.getString("role"));
               return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
