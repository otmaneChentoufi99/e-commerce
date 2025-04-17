package com.example.ecommerce_app.controller;

import com.example.ecommerce_app.dao.CategoryDAO;
import com.example.ecommerce_app.dao.ProductDAO;
import com.example.ecommerce_app.model.Category;
import com.example.ecommerce_app.model.Product;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

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
    @FXML private ComboBox<Category> categoryComboBox;
    @FXML private TextField quantityField;
    @FXML private CheckBox availableCheckBox;
    @FXML private Button saveButton;
    @FXML private Label formTitleLabel;
    @FXML private TableColumn<Product, Void> deleteColumn;
    @FXML private ImageView imageView;

    private byte[] selectedImageBytes;  // To store the selected image as a byte array
    private Product selectedProduct;
    private final ProductDAO productDAO = new ProductDAO(); // adjust based on your setup
    private final CategoryDAO categoryDAO = new CategoryDAO(); // adjust based on your setup


    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        availableColumn.setCellValueFactory(new PropertyValueFactory<>("available"));

        setDeleteButtonColumn();
        loadProducts();
        loadCategories(); // 👈 Add this
    }

    private void loadCategories() {
        List<Category> categories = categoryDAO.getAllCategories();
        categoryComboBox.setItems(FXCollections.observableArrayList(categories));
    }


    @FXML
    private void onChooseImage() {
        // Open a file chooser dialog to select the image
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.gif"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            try {
                // Convert the selected image to byte array
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                selectedImageBytes = fileInputStream.readAllBytes();
                fileInputStream.close();

                // Display the image in the ImageView
                Image image = new Image(new FileInputStream(selectedFile));
                imageView.setImage(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadProducts() {
        List<Product> productList = productDAO.getAllProducts();
        productsTable.setItems(FXCollections.observableArrayList(productList));
    }


    @FXML
    private void onProductSelected() {
        selectedProduct = productsTable.getSelectionModel().getSelectedItem();

        if (selectedProduct != null) {
            nameField.setText(selectedProduct.getName());
            priceField.setText(String.valueOf(selectedProduct.getPrice()));
            categoryComboBox.setValue(selectedProduct.getCategory());
            quantityField.setText(String.valueOf(selectedProduct.getQuantity()));
            availableCheckBox.setSelected(selectedProduct.isAvailable());

                // Assuming the product has an image as a byte array
                byte[] imageBytes = selectedProduct.getImage();

                if (imageBytes != null && imageBytes.length > 0) {
                    // Convert the byte array to an Image and set it to the ImageView
                    Image image = new Image(new ByteArrayInputStream(imageBytes));

                    // Make sure the UI is updated on the JavaFX Application Thread
                    Platform.runLater(() -> {
                        imageView.setImage(image);
                    });
                } else {
                    // Set a placeholder image or clear the ImageView if no image is found
                    Platform.runLater(() -> {
                        imageView.setImage(null); // or set a default image
                    });
                }

            saveButton.setText("Update Product");
            formTitleLabel.setText("Edit Product");
        }
    }


    @FXML
    private void onSaveProduct() {
        // Retrieve data from the form
        String name = nameField.getText();
        double price = Double.parseDouble(priceField.getText());
        Category category = categoryComboBox.getValue();
        int quantity = Integer.parseInt(quantityField.getText());
        boolean available = availableCheckBox.isSelected();

        // Use selectedImageBytes or fall back to selectedProduct’s image
        byte[] image = selectedImageBytes != null ? selectedImageBytes :
                (selectedProduct != null ? selectedProduct.getImage() : new byte[0]);

        if (selectedProduct == null) {
            // ➕ New product
            Product newProduct = new Product(name, price, category, available, quantity, image);
            productDAO.saveProduct(newProduct);
        } else {
            // 🔁 Update existing product
            selectedProduct.setName(name);
            selectedProduct.setPrice(price);
            selectedProduct.setCategory(category);
            selectedProduct.setQuantity(quantity);
            selectedProduct.setAvailable(available);
            selectedProduct.setImage(image); // update the image if changed

            productDAO.updateProduct(selectedProduct); // Make sure you have this method
        }

        loadProducts();
        clearForm();
    }
    @FXML
    private void onClearForm() {
        clearForm();
    }

    private void clearForm() {
        nameField.clear();
        priceField.clear();
        categoryField.clear();
        quantityField.clear();
        availableCheckBox.setSelected(false);
        selectedProduct = null;
        selectedImageBytes = null; // 👈 Important fix here
        productsTable.getSelectionModel().clearSelection();
        imageView.setImage(null);

        saveButton.setText("Add Product");
        formTitleLabel.setText("Add New Product");
    }


    private void setDeleteButtonColumn() {
        deleteColumn.setCellFactory(new Callback<TableColumn<Product, Void>, TableCell<Product, Void>>() {
            @Override
            public TableCell<Product, Void> call(TableColumn<Product, Void> param) {
                final TableCell<Product, Void> cell = new TableCell<Product, Void>() {
                    private final Button deleteButton = new Button("Delete");

                    {
                        deleteButton.setPrefWidth(120); // Set width
                        deleteButton.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white;");
                        deleteButton.setOnAction(event -> {
                            Product product = getTableRow().getItem();
                            if (product != null) {
                                onDeleteProduct(product);
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(deleteButton);
                        }
                    }
                };
                return cell;
            }
        });
    }

    @FXML
    private void onDeleteProduct(Product product) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Product");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this product?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            productDAO.deleteProduct(product.getId());
            productsTable.getItems().remove(product);
            loadProducts();
            clearForm();
        }
    }
}
