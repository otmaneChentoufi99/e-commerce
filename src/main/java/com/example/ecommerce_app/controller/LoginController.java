package com.example.ecommerce_app.controller;

import com.example.ecommerce_app.dao.UserDAO;
import com.example.ecommerce_app.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    public TextField usernameField;
    public PasswordField passwordField;

    public void handleLogin(ActionEvent event) throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        User user = UserDAO.login(username, password);

        if (user == null) {
            System.out.println("Invalid credentials.");
            return;
        }

        String role = user.getRole();
        String fxmlPath = "";
        String title = "";

        switch (role) {
            case "admin":
                fxmlPath = "/com/example/ecommerce_app/admin_dashboard.fxml";
                title = "Admin Dashboard";
                break;
            case "delivery":
                fxmlPath = "/com/example/ecommerce_app/delivery_dashboard.fxml";
                title = "Delivery Dashboard";
                break;
            default:
                System.out.println("Unknown role: " + role);
                return;
        }

        Parent page = FXMLLoader.load(getClass().getResource(fxmlPath));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(page));
        stage.setTitle(title);
        stage.show();
    }
}
