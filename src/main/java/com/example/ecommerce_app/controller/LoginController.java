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

                Parent adminPage = FXMLLoader.load(getClass().getResource(fxmlPath));
                Stage adminStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                adminStage.setScene(new Scene(adminPage));
                adminStage.setTitle(title);
                adminStage.show();
                break;

            case "delivery":
                fxmlPath = "/com/example/ecommerce_app/delivery_dashboard.fxml";
                title = "Delivery Dashboard";

                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent deliveryPage = loader.load();

                DeliveryDashboardController controller = loader.getController();
                System.out.println(user.getUsername());
                System.out.println(user.getId());
                controller.setDeliveryPersonId(user.getId()); // assuming User is the DeliveryPerson

                Stage deliveryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                deliveryStage.setScene(new Scene(deliveryPage));
                deliveryStage.setTitle(title);
                deliveryStage.show();
                break;

            default:
                System.out.println("Unknown role: " + role);
                return;
        }
    }
}
