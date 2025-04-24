package com.example.ecommerce_app.controller;

import com.example.ecommerce_app.dao.DeliveryPersonDAO;
import com.example.ecommerce_app.dao.OrderDAO;
import com.example.ecommerce_app.model.DeliveryPerson;
import com.example.ecommerce_app.model.Order;
import com.example.ecommerce_app.model.OrderStatus;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class OrderListController implements Initializable {

    @FXML private TableView<Order> ordersTable;
    @FXML private TableColumn<Order, Long> idColumn;
    @FXML private TableColumn<Order, String> fullNameColumn;
    @FXML private TableColumn<Order, String> addressColumn;
    @FXML private TableColumn<Order, OrderStatus> statusColumn;
    @FXML private TableColumn<Order, String> deliveryPersonColumn;
    @FXML private TableColumn<Order, Void> assignColumn;
    @FXML private TableColumn<Order, String> paymentMethodColumn;
    @FXML private TableColumn<Order, LocalDateTime> orderDateColumn;
    @FXML private TableColumn<Order, Integer> itemsColumn;

    private final OrderDAO orderDAO = new OrderDAO();
    private final DeliveryPersonDAO deliveryPersonDAO = new DeliveryPersonDAO();
    private ObservableList<Order> orders;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureColumns();
        loadData();
        addAssignButtonToTable();
    }

    private void configureColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        paymentMethodColumn.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        orderDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        itemsColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getItems().size()).asObject());

        statusColumn.setCellFactory(column -> new TableCell<Order, OrderStatus>() {
            @Override
            protected void updateItem(OrderStatus status, boolean empty) {
                super.updateItem(status, empty);

                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status.toString());

                    // Color logic
                    switch (status) {
                        case PENDING:
                            setStyle("-fx-background-color: orange; -fx-text-fill: white;");
                            break;
                        case DISTRIBUTING:
                            setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                            break;
                        case DELIVERED:
                            setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
                            break;
                        case CANCELLED:
                            setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                            break;
                        default:
                            setStyle("");
                            break;
                    }
                }
            }
        });


        // Delivery Person column
        deliveryPersonColumn.setCellValueFactory(cellData -> {
            DeliveryPerson dp = cellData.getValue().getDeliveryPerson();
            if (dp != null) {
                return new SimpleStringProperty(dp.getUsername() + " (" + dp.getArea() + ")");
            }
            return new SimpleStringProperty("Not assigned");
        });

        // Date formatting
        orderDateColumn.setCellFactory(column -> new TableCell<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            @Override
            protected void updateItem(LocalDateTime date, boolean empty) {
                super.updateItem(date, empty);
                setText(empty || date == null ? null : formatter.format(date));
            }
        });
    }

    private void addAssignButtonToTable() {
        assignColumn.setCellFactory(param -> new TableCell<>() {
            private final Button assignButton = new Button();

            {
                assignButton.setOnAction(event -> {
                    Order order = getTableView().getItems().get(getIndex());
                    showAssignmentDialog(order);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Order order = getTableView().getItems().get(getIndex());
                    assignButton.setText(order.getDeliveryPerson() != null ? "Reassign" : "Assign");
                    setGraphic(assignButton);
                }
            }
        });
    }

    private void showAssignmentDialog(Order order) {
        Dialog<DeliveryPerson> dialog = new Dialog<>();
        dialog.setTitle(order.getDeliveryPerson() != null ? "Reassign Delivery Person" : "Assign Delivery Person");
        dialog.setHeaderText("Order #" + order.getId() + " - " + (order.getDeliveryPerson() != null ?
                "Currently assigned to: " + order.getDeliveryPerson().getUsername() + " (" + order.getDeliveryPerson().getArea() + ")" :
                "No current assignment"));

        ButtonType assignButtonType = new ButtonType(order.getDeliveryPerson() != null ? "Reassign" : "Assign", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(assignButtonType, ButtonType.CANCEL);

        ListView<DeliveryPerson> deliveryListView = new ListView<>();
        deliveryListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(DeliveryPerson dp, boolean empty) {
                super.updateItem(dp, empty);
                setText(empty || dp == null ? null : dp.getUsername() + " - " + dp.getArea());
            }
        });

        ObservableList<DeliveryPerson> deliveryPersons = FXCollections.observableArrayList(
                DeliveryPersonDAO.getAllDeliveries()
        );
        deliveryListView.setItems(deliveryPersons);

        if (order.getDeliveryPerson() != null) {
            deliveryListView.getSelectionModel().select(order.getDeliveryPerson());
        }

        ScrollPane scrollPane = new ScrollPane(deliveryListView);
        scrollPane.setPrefViewportHeight(200);
        scrollPane.setFitToWidth(true);

        VBox content = new VBox(10);
        content.getChildren().addAll(
                new Label("Available Delivery Persons:"),
                scrollPane
        );
        content.setPadding(new Insets(10));

        dialog.getDialogPane().setContent(content);

        // Add result converter
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == assignButtonType) {
                return deliveryListView.getSelectionModel().getSelectedItem();
            }
            return null;
        });

        // Show dialog and handle result
        Optional<DeliveryPerson> result = dialog.showAndWait();
        result.ifPresent(deliveryPerson -> {
            orderDAO.assignDeliveryPerson(order.getId(), deliveryPerson.getId());
            order.setDeliveryPerson(deliveryPerson);
            ordersTable.refresh();
        });
    }
    @FXML
    private void refreshOrders() {
        orders.setAll(orderDAO.getAllOrders());
    }

    @FXML
    private void showAssignedOrders() {
        orders.setAll(orderDAO.getAllOrders().stream()
                .filter(order -> order.getDeliveryPerson() != null)
                .collect(Collectors.toList()));
    }

    @FXML
    private void showUnassignedOrders() {
        orders.setAll(orderDAO.getAllOrders().stream()
                .filter(order -> order.getDeliveryPerson() == null)
                .collect(Collectors.toList()));
    }

    private void loadData() {
        orders = FXCollections.observableArrayList(orderDAO.getAllOrders());
        ordersTable.setItems(orders);
    }
}