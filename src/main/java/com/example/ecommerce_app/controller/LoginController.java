package com.example.ecommerce_app.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;

public class LoginController {

    public javafx.scene.control.TextField usernameField;
    public javafx.scene.control.PasswordField passwordField;

    public void handleLogin(ActionEvent event) throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Simple check for example purpose
        if ("admin".equals(username) && "1234".equals(password)) {
            // Load the home.fxml
            Parent homePage = FXMLLoader.load(getClass().getResource("/com/example/ecommerce_app/dashboard-view.fxml"));
            // Get the stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the scene
            stage.setScene(new Scene(homePage));
            stage.setTitle("Home Page");
            stage.show();
        } else {
            System.out.println("Invalid credentials.");
        }
    }
}
