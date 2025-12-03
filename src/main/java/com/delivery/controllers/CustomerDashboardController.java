package com.delivery.controllers;

import com.delivery.dao.OrderDAO;
import com.delivery.dao.ProductDAO;
import com.delivery.models.Customer;
import com.delivery.models.Order;
import com.delivery.models.Product;
import com.delivery.util.AlertUtil;
import com.delivery.util.SessionHolder;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class CustomerDashboardController {

    @FXML private Label welcomeLabel;

    // Product Table
    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, Integer> colProdId;
    @FXML private TableColumn<Product, String> colProdTitle;
    @FXML private TableColumn<Product, String> colProdDesc;
    @FXML private TableColumn<Product, Double> colProdPrice;
    @FXML private TableColumn<Product, Integer> colProdStock;

    // Orders Table
    @FXML private TableView<Order> orderTable;
    @FXML private TableColumn<Order, Integer> colOrderId;
    @FXML private TableColumn<Order, Double> colOrderAmount;
    @FXML private TableColumn<Order, String> colOrderStatus;

    private final ProductDAO productDAO = new ProductDAO();
    private final OrderDAO orderDAO = new OrderDAO();

    private final ObservableList<Product> productList = FXCollections.observableArrayList();
    private final ObservableList<Order> orderList = FXCollections.observableArrayList();

    private Customer currentCustomer;

    @FXML
    public void initialize() {

        currentCustomer = SessionHolder.getCustomer();
        if (currentCustomer != null) {
            welcomeLabel.setText("Welcome, " + currentCustomer.getUsername());
        }

        // Product table bindings
        colProdId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()).asObject());
        colProdTitle.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitle()));
        colProdDesc.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescription()));
        colProdPrice.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getPrice()).asObject());
        colProdStock.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getStock()).asObject());
        productTable.setItems(productList);

        // Order table bindings
        colOrderId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()).asObject());
        colOrderAmount.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getTotalAmount()).asObject());
        colOrderStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus().name()));
        orderTable.setItems(orderList);

        loadProducts();
        loadOrders();
    }

    private void loadProducts() {
        productList.setAll(productDAO.findAll());
    }

    private void loadOrders() {
        if (currentCustomer != null) {
            orderList.setAll(orderDAO.findByCustomerId(currentCustomer.getId()));
        }
    }

    @FXML
    private void onPlaceOrder() {
        Product p = productTable.getSelectionModel().getSelectedItem();

        if (p == null) {
            AlertUtil.error("Error", "Please select a product");
            return;
        }

        if (p.getStock() <= 0) {
            AlertUtil.error("Error", "Product is out of stock");
            return;
        }

        // Create order + reduce stock
        orderDAO.createSimpleOrder(currentCustomer.getId(), p.getId(), p.getPrice());

        AlertUtil.info("Success", "Order placed successfully!");

        loadProducts(); // update stock
        loadOrders();   // show new order
    }

    @FXML
    private void onLogout() {
        try {
            SessionHolder.clearSession();
            com.delivery.App.setRoot("Login", "/fxml/login.fxml");
        } catch (Exception e) {
            AlertUtil.error("Error", "Failed to logout: " + e.getMessage());
        }
    }
}
