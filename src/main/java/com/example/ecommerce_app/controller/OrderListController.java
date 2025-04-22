package com.example.ecommerce_app.controller;

import com.example.ecommerce_app.dao.OrderDAO;
import com.example.ecommerce_app.model.Order;
import com.example.ecommerce_app.model.OrderItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDateTime;
import java.util.List;

public class OrderListController {

    private final OrderDAO orderDAO = new OrderDAO();

    @FXML
    private TableView<Order> orderTable;

    @FXML
    private TableColumn<Order, Long> idColumn;

    @FXML
    private TableColumn<Order, String> nameColumn;

    @FXML
    private TableColumn<Order, String> phoneColumn;

    @FXML
    private TableColumn<Order, String> paymentColumn;

    @FXML
    private TableColumn<Order, LocalDateTime> dateColumn;

    @FXML
    private TableColumn<Order, String> itemsColumn;


    private ObservableList<Order> orderData;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        paymentColumn.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        itemsColumn.setCellValueFactory(cellData -> {
            Order order = cellData.getValue();
            List<OrderItem> items = order.getItems();
            StringBuilder sb = new StringBuilder();
            for (OrderItem item : items) {
                sb.append(item.getProductName()).append(" x").append(item.getQuantity()).append(", ");
            }
            String itemsString = sb.toString().trim();
            if (itemsString.endsWith(",")) {
                itemsString = itemsString.substring(0, itemsString.length() - 1);
            }
            return new javafx.beans.property.SimpleStringProperty(itemsString);
        });


        loadOrders();
    }

    private void loadOrders() {
        List<Order> orders = orderDAO.getAllOrders();
        orderData = FXCollections.observableArrayList(orders);
        orderTable.setItems(orderData);
    }
}
