module com.example.ecommerce_app {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.example.ecommerce_app.controller to javafx.fxml;
    opens com.example.ecommerce_app.model to javafx.base;

    exports com.example.ecommerce_app;

    exports com.example.ecommerce_app.controller;
    exports com.example.ecommerce_app.model;
}
