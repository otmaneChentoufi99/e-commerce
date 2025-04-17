package com.example.ecommerce_app.controller;
import com.example.ecommerce_app.dao.OrderDAO;
import com.example.ecommerce_app.dao.ProductDAO;
import com.example.ecommerce_app.model.Category;
import com.example.ecommerce_app.model.Order;
import com.example.ecommerce_app.model.OrderItem;
import com.example.ecommerce_app.model.Product;
import com.example.ecommerce_app.service.CartService;
import com.example.ecommerce_app.service.ProductService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.geometry.*;

import java.io.ByteArrayInputStream;
import java.util.*;
public class StoreController {


    @FXML private HBox navbar;
    @FXML private FlowPane productContainer;

    private final ProductService productService = new ProductService();
    private final OrderDAO orderDAO = new OrderDAO();



    public void initialize() {
        loadCategories();
        setupCartIcon();
    }

    private void loadCategories() {
        List<Category> categories = productService.getCategories();
        for (Category category : categories) {
            Button btn = new Button(category.getName());
            btn.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-text-fill: black;" +
                            "-fx-cursor: hand;" +
                            "-fx-font-size: 18px;" +
                            "-fx-font-weight: medium;" +
                            "-fx-padding: 0 5 0 5;"
            );
            btn.setOnAction(e -> loadProductsByCategory(category.getName()));
            navbar.getChildren().add(btn);
        }
    }

    private void setupCartIcon() {
        Button cartBtn = new Button("🛒(0)");
        cartBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-cursor: hand;" +
                        "-fx-font-size: 16px;" +
                        "-fx-text-fill: #333;" +
                        "-fx-padding: 5 10 5 10;" +
                        "-fx-border-radius: 15px;" +
                        "-fx-border-color: #ccc;" +
                        "-fx-border-width: 2px;" +
                        "-fx-background-radius: 15px;" +
                        "-fx-font-family: 'Segoe UI', sans-serif;"
        );

        cartBtn.setOnAction(e -> openCartPage());
        navbar.getChildren().add(cartBtn);
        CartService.setCartButton(cartBtn);
    }

    private void loadProductsByCategory(String category) {
        productContainer.getChildren().clear();
        List<Product> products = productService.getProductsByCategory(category);
        System.out.println("Loading products for category: " + category + ", found: " + products.size());

        for (Product p : products) {
            VBox card = createProductCard(p);
            productContainer.getChildren().add(card);
        }
    }

    private VBox createProductCard(Product product) {
        ByteArrayInputStream bis = new ByteArrayInputStream(product.getImage());
        ImageView imageView = new ImageView(new Image(bis));
        imageView.setFitWidth(200);
        imageView.setPreserveRatio(true);

        Label nameLabel = new Label(product.getName());
        Label priceLabel = new Label("$" + product.getPrice());

        Button addToCartButton = new Button("Add to Cart");
        addToCartButton.setOnAction(e -> CartService.addToCart(product));

        Button orderButton = new Button("Order Now");
        orderButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        orderButton.setOnAction(e -> showOrderDialog(product));  // Single product

        VBox card = new VBox(10, imageView, nameLabel, priceLabel, addToCartButton, orderButton);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-border-color: lightgray; -fx-border-radius: 10;");

        return card;
    }

    private void showOrderDialog(Product product) {
        showOrderDialog(Collections.singletonList(product));
    }

    private void showOrderDialog(List<Product> products) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Place Your Order");

        TextField fullNameField = new TextField();
        TextField phoneField = new TextField();
        TextArea addressArea = new TextArea();
        TextArea commentArea = new TextArea();

        fullNameField.setPromptText("Full Name");
        phoneField.setPromptText("Phone Number");
        addressArea.setPromptText("Address");
        commentArea.setPromptText("Additional Comments");

        addressArea.setPrefRowCount(2);
        commentArea.setPrefRowCount(2);

        VBox content = new VBox(10,
                new Label("Full Name:"), fullNameField,
                new Label("Phone Number:"), phoneField,
                new Label("Address:"), addressArea,
                new Label("Comment:"), commentArea
        );
        content.setPadding(new Insets(10));

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String fullName = fullNameField.getText().trim();
            String phone = phoneField.getText().trim();
            String address = addressArea.getText().trim();
            String comment = commentArea.getText().trim();

            if (fullName.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                showAlert("Missing Information", "Please fill in all required fields.");
                return;
            }

            try {
                Order order = new Order(fullName, phone, address, comment, "Cash on Delivery");

                for (Product product : products) {
                    int quantity = product.getQuantity();
                    order.addItem(new OrderItem(product.getId(), quantity));
                }

                orderDAO.saveOrder(order);
                CartService.clearCart(); // Optional
                showAlert("Order Placed", "Your order has been received. Thank you!");
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Order Failed", "An error occurred while placing your order.");
            }
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private void openCartPage() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Cart");

        VBox cartBox = new VBox(10);
        cartBox.setPadding(new Insets(10));

        for (Product p : CartService.getCartItems()) {
            ImageView imageView = new ImageView(new Image(new ByteArrayInputStream(p.getImage())));
            imageView.setFitWidth(50);
            imageView.setFitHeight(50);

            Label nameLabel = new Label(p.getName());
            Label categoryLabel = new Label("Category: " + p.getCategory());
            Spinner<Integer> quantitySpinner = new Spinner<>(1, 5, p.getQuantity());
            quantitySpinner.setEditable(true);

            Label quantityLabel = new Label("Qty: ");
            quantityLabel.setLabelFor(quantitySpinner);
            Button deleteButton = new Button("Remove");

            quantitySpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
                p.setQuantity(newValue); // Update the product quantity in the cart
                CartService.updateProductQuantity(p); // Update quantity in CartService if needed
            });

            deleteButton.setOnAction(e -> {
                CartService.removeFromCart(p);
                dialog.close();  // Close and reopen to refresh UI
                openCartPage();
            });

            HBox itemRow = new HBox(10, imageView, nameLabel, categoryLabel ,quantityLabel, quantitySpinner, deleteButton);
            itemRow.setAlignment(Pos.CENTER_LEFT);
            cartBox.getChildren().add(itemRow);
        }

        Button orderButton = new Button("Place Order");
        orderButton.setStyle("-fx-background-color: green; -fx-text-fill: white;");
        orderButton.setOnAction(e -> {
            List<Product> cartItems = CartService.getCartItems();
            if (!cartItems.isEmpty()) {
                showOrderDialog(cartItems);  // List of products
                dialog.close();
            } else {
                showAlert("Cart Empty", "There are no items in the cart.");
            }
        });

        VBox dialogContent = new VBox(10, cartBox, orderButton);
        dialog.getDialogPane().setContent(dialogContent);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        dialog.showAndWait();
    }
}

