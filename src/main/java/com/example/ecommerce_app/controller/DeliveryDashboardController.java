package com.example.ecommerce_app.controller;

import com.example.ecommerce_app.dao.OrderDAO;
import com.example.ecommerce_app.model.Order;
import com.example.ecommerce_app.model.OrderStatus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Node;

import java.io.IOException;
import java.util.List;

public class DeliveryDashboardController {

    @FXML private TableView<Order> orderTable;
    @FXML private TableColumn<Order, Long> idColumn;
    @FXML private TableColumn<Order, String> nameColumn;
    @FXML private TableColumn<Order, String> addressColumn;
    @FXML private TableColumn<Order, OrderStatus> statusColumn;
    @FXML private TableColumn<Order, Void> actionColumn;

    private final OrderDAO orderDAO = new OrderDAO();
    private ObservableList<Order> orderData;

    private int deliveryPersonId; // Set this from login

    public void setDeliveryPersonId(int id) {
        this.deliveryPersonId = id;
        System.out.println(id);
        loadOrders();
    }

    @FXML
    public void initialize() {
        setupTableColumns();
    }

    private void loadOrders() {
        List<Order> orders = orderDAO.getOrdersByDeliveryPerson(deliveryPersonId);
        orderData = FXCollections.observableArrayList(orders);
        orderTable.setItems(orderData);
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final ComboBox<OrderStatus> statusComboBox = new ComboBox<>();

            {
                statusComboBox.setItems(FXCollections.observableArrayList(OrderStatus.values()));
                statusComboBox.setOnAction(e -> {
                    Order order = getTableView().getItems().get(getIndex());
                    OrderStatus newStatus = statusComboBox.getValue();
                    if (newStatus != null) {
                        order.setStatus(newStatus);
                        orderDAO.updateOrderStatus(order.getId(), newStatus); // Update in DB
                        loadOrders(); // Refresh
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Order order = getTableView().getItems().get(getIndex());
                    statusComboBox.setValue(order.getStatus());
                    setGraphic(statusComboBox);
                }
            }
        });
    }
}
