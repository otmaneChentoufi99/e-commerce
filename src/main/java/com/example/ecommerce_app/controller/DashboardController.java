package com.example.ecommerce_app.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {


    @FXML
    private StackPane contentArea;

    public void loadHome() {
        loadUI("/com/example/ecommerce_app/home.fxml");
    }

    public void loadProducts() {

        loadUI("/com/example/ecommerce_app/products.fxml");
    }

    public void loadOrderList() {
        loadUI("/com/example/ecommerce_app/orders_list.fxml");
    }


    public void loadDeliveryList() {
        loadUI("/com/example/ecommerce_app/delivery_list.fxml");
    }

    public void onLogOut() {
        try {
            // Load the login view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ecommerce_app/login-view.fxml"));
            Parent root = loader.load();

            // Get the current stage (window) from any UI element
            Stage stage = (Stage) contentArea.getScene().getWindow(); // Replace 'someNodeInCurrentScene' with any known node
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void loadUI(String fxmlPath) {
        try {
            Node node = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().setAll(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
