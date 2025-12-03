package com.delivery.controllers;

import com.delivery.dao.OrderDAO;
import com.delivery.models.DeliveryMan;
import com.delivery.models.Order;
import com.delivery.models.OrderStatus;
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

public class DeliveryDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private TableView<Order> orderTable;
    @FXML private TableColumn<Order, Integer> colOrderId;
    @FXML private TableColumn<Order, Integer> colOrderCustId;
    @FXML private TableColumn<Order, String> colOrderStatus;
    @FXML private TableColumn<Order, Double> colOrderAmount;

    private final OrderDAO orderDAO = new OrderDAO();
    private final ObservableList<Order> orderList = FXCollections.observableArrayList();
    private DeliveryMan currentDeliveryMan;

    @FXML
    public void initialize() {
        // Load current logged-in delivery man
        currentDeliveryMan = SessionHolder.getDeliveryMan();
        if (currentDeliveryMan != null) {
            welcomeLabel.setText("Welcome, " + currentDeliveryMan.getUsername());
        } else {
            welcomeLabel.setText("Welcome, Delivery Person");
        }

        // Table column bindings
        colOrderId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()).asObject());
        colOrderCustId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getCustomerId()).asObject());
        colOrderStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus().name()));
        colOrderAmount.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getTotalAmount()).asObject());

        orderTable.setItems(orderList);
        loadOrders();
    }

    private void loadOrders() {
        try {
            if (currentDeliveryMan == null) {
                AlertUtil.error("Error", "No delivery man session found. Please login again.");
                return;
            }
            orderList.setAll(orderDAO.findByDeliveryManId(currentDeliveryMan.getId()));
        } catch (Exception e) {
            AlertUtil.error("Error", "Failed to load orders: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onMarkOutForDelivery() {
        Order o = orderTable.getSelectionModel().getSelectedItem();
        if (o == null) {
            AlertUtil.error("Error", "Please select an order");
            return;
        }

        // Check if order is in a valid state to be marked as out for delivery
        if (o.getStatus() == OrderStatus.DELIVERED) {
            AlertUtil.error("Error", "This order has already been delivered");
            return;
        }
        if (o.getStatus() == OrderStatus.OUT_FOR_DELIVERY) {
            AlertUtil.info("Info", "This order is already out for delivery");
            return;
        }

        try {
            orderDAO.updateStatus(o.getId(), OrderStatus.OUT_FOR_DELIVERY);
            AlertUtil.info("Success", "Order marked as out for delivery");
            loadOrders();
        } catch (Exception e) {
            AlertUtil.error("Error", "Failed to update order status: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onMarkDelivered() {
        Order o = orderTable.getSelectionModel().getSelectedItem();
        if (o == null) {
            AlertUtil.error("Error", "Please select an order");
            return;
        }

        // Check if order is already delivered
        if (o.getStatus() == OrderStatus.DELIVERED) {
            AlertUtil.info("Info", "This order has already been delivered");
            return;
        }

        try {
            orderDAO.updateStatus(o.getId(), OrderStatus.DELIVERED);
            AlertUtil.info("Success", "Order marked as delivered");
            loadOrders();
        } catch (Exception e) {
            AlertUtil.error("Error", "Failed to update order status: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onLogout() {
        try {
            SessionHolder.clearSession();
            com.delivery.App.setRoot("Login", "/fxml/login.fxml");
        } catch (Exception e) {
            AlertUtil.error("Error", "Failed to logout: " + e.getMessage());
            e.printStackTrace();
        }
    }
}