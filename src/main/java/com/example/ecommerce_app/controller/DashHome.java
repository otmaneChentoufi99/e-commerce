package com.example.ecommerce_app.controller;

import com.example.ecommerce_app.dao.OrderDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class DashHome implements Initializable {

    @FXML
    private PieChart orderPieChart;

    @FXML
    private BarChart<String, Number> salesBarChart;

    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadOrderStats();
        loadSalesStats();
    }

    private void loadOrderStats() {
        Map<String, Integer> stats = orderDAO.getOrderStatusCounts();
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();

        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            pieData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        orderPieChart.setData(pieData);
        orderPieChart.setTitle("Order Status");
    }

    private void loadSalesStats() {
        Map<String, Integer> stats = orderDAO.getMonthlySales();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Monthly Sales");

        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        salesBarChart.getData().add(series);
    }
}
