package com.example.ecommerce_app.controller;

import com.example.ecommerce_app.dao.DeliveryPersonDAO;
import com.example.ecommerce_app.model.DeliveryPerson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.util.List;
import java.util.Optional;

public class DeliveryListController {

    @FXML
    private TableView<DeliveryPerson> deliveryTable;

    @FXML
    private TableColumn<DeliveryPerson, Integer> idColumn;

    @FXML
    private TableColumn<DeliveryPerson, String> usernameColumn;

    @FXML
    private TableColumn<DeliveryPerson, String> phoneColumn;

    @FXML
    private TableColumn<DeliveryPerson, String> areaColumn;

    @FXML private TableColumn<DeliveryPerson, Void> deleteColumn;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField areaField;

    private ObservableList<DeliveryPerson> deliveries;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        areaColumn.setCellValueFactory(new PropertyValueFactory<>("area"));
        setDeleteButtonColumn();

        loadDeliveries();

        // Optional: Populate fields when row selected
        deliveryTable.setOnMouseClicked(e -> populateFields());
    }

    private void setDeleteButtonColumn() {
        deleteColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<DeliveryPerson, Void> call(final TableColumn<DeliveryPerson, Void> param) {
                return new TableCell<>() {
                    private final Button deleteButton = new Button("Delete");

                    {
                        deleteButton.setPrefWidth(80);
                        deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                        deleteButton.setOnAction(event -> {
                            DeliveryPerson deliveryPerson = getTableView().getItems().get(getIndex());
                            if (deliveryPerson != null) {
                                onDeleteDeliveryPerson(deliveryPerson);
                            }
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(deleteButton);
                        }
                    }
                };
            }
        });
    }

    @FXML
    private void onDeleteDeliveryPerson(DeliveryPerson deliveryPerson) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Product");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this delivery person?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            System.out.println(deliveryPerson.getId());
            DeliveryPersonDAO.deleteDelivery(deliveryPerson.getId());
            deliveryTable.getItems().remove(deliveryPerson);
            loadDeliveries();
            clearFields();
        }
    }


    private void loadDeliveries() {
        List<DeliveryPerson> deliveryList = DeliveryPersonDAO.getAllDeliveries();
        deliveries = FXCollections.observableArrayList(deliveryList);
        deliveryTable.setItems(deliveries);
    }

    @FXML
    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        phoneField.clear();
        areaField.clear();
    }

    private void populateFields() {
        DeliveryPerson selected = deliveryTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            usernameField.setText(selected.getUsername());
            phoneField.setText(selected.getPhone());
            areaField.setText(selected.getArea());
            passwordField.clear(); // Password won't be loaded from DB
        }
    }

    @FXML
    public void handleAdd() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String phone = phoneField.getText();
        String area = areaField.getText();

        if (username.isEmpty() || password.isEmpty() || phone.isEmpty() || area.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "All fields must be filled.");
            return;
        }

        DeliveryPerson newDelivery = new DeliveryPerson();
        newDelivery.setUsername(username);
        newDelivery.setPassword(password); // needed for add
        newDelivery.setPhone(phone);
        newDelivery.setArea(area);

        DeliveryPersonDAO.addDelivery(newDelivery);
        loadDeliveries();
        clearFields();
    }

    @FXML
    public void handleUpdate() {
        DeliveryPerson selected = deliveryTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No delivery person selected.");
            return;
        }

        selected.setUsername(usernameField.getText());
        selected.setPhone(phoneField.getText());
        selected.setArea(areaField.getText());

        DeliveryPersonDAO.updateDelivery(selected);
        loadDeliveries();
        clearFields();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Notification");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
