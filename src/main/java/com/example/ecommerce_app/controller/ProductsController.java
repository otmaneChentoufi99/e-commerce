package com.example.ecommerce_app.controller;

import com.example.ecommerce_app.model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class ProductsController {

    @FXML private TableView<Product> productsTable;
    @FXML private TableColumn<Product, String> nameColumn;
    @FXML private TableColumn<Product, Double> priceColumn;
    @FXML private TableColumn<Product, String> categoryColumn;
    @FXML private TableColumn<Product, Boolean> availableColumn;
    @FXML private TableColumn<Product, Integer> quantityColumn;

    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private TextField categoryField;
    @FXML private TextField quantityField;
    @FXML private CheckBox availableCheckBox;


    private Product selectedProduct;

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("Name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("Price"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("Category"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        availableColumn.setCellValueFactory(new PropertyValueFactory<>("available"));


        // Example: Load products into the tabl
        productsTable.setItems(FXCollections.observableArrayList(
                new Product("Sneakers", 299.99, "Men", true, 50),
                new Product("Running Shoes", 199.99, "Women", false, 30),
                new Product("Leather Jacket", 499.50, "Men", true, 20),
                new Product("Handbag", 249.99, "Women", true, 40),
                new Product("Smart Watch", 149.99, "Electronics", true, 25),
                new Product("Wireless Earbuds", 89.99, "Electronics", true, 60),
                new Product("Kids T-shirt", 29.99, "Kids", false, 70),
                new Product("Baby Shoes", 39.99, "Kids", true, 35),
                new Product("Sports Socks (Pack of 3)", 15.99, "Accessories", true, 80),
                new Product("Backpack", 69.99, "Accessories", true, 45),
                new Product("Sunglasses", 59.99, "Accessories", false, 55),
                new Product("Formal Shoes", 179.99, "Men", true, 22),
                new Product("Heels", 129.99, "Women", true, 33),
                new Product("Graphic Hoodie", 79.99, "Men", false, 44),
                new Product("Yoga Pants", 59.99, "Women", true, 20)
        ));
    }

    @FXML
    private void onProductSelected() {
        selectedProduct = productsTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            nameField.setText(selectedProduct.getName());
            priceField.setText(String.valueOf(selectedProduct.getPrice()));
            categoryField.setText(selectedProduct.getCategory());
            quantityField.setText(String.valueOf(selectedProduct.getQuantity()));  // Update quantity
            availableCheckBox.setSelected(selectedProduct.isAvailable());  // Update availability
        }
    }

    @FXML
    private void onUpdateProduct() {
        if (selectedProduct != null) {
            selectedProduct.setName(nameField.getText());
            selectedProduct.setPrice(Double.parseDouble(priceField.getText()));
            selectedProduct.setCategory(categoryField.getText());

            // Update quantity and availability
            selectedProduct.setQuantity(Integer.parseInt(quantityField.getText()));  // Update quantity
            selectedProduct.setAvailable(availableCheckBox.isSelected());  // Update availability

            // Refresh table view
            productsTable.refresh();
            clearForm(); // Clear the form after update
        }
    }


    private void clearForm() {
        nameField.clear();
        priceField.clear();
        categoryField.clear();
        quantityField.clear();
        availableCheckBox.setSelected(false);  // Uncheck the checkbox
        selectedProduct = null;
    }

    @FXML
    private void onClearForm() {
        clearForm();
        productsTable.getSelectionModel().clearSelection();
    }

}
