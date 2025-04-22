package com.example.ecommerce_app.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Node;

import java.io.IOException;

public class DeliveryDashboardController {

    @FXML
    private Label areaLabel;

    private String deliveryArea;

    public void setDeliveryArea(String area) {
        this.deliveryArea = area;
        areaLabel.setText("Area: " + area);
    }

    public void handleViewDeliveries(ActionEvent event) {
        System.out.println("Viewing deliveries in area: " + deliveryArea);
        // Future: load delivery list view
    }

    public void handleLogout(ActionEvent event) throws IOException {
        Parent loginPage = FXMLLoader.load(getClass().getResource("/com/example/ecommerce_app/login.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(loginPage));
        stage.setTitle("Login");
        stage.show();
    }
}
