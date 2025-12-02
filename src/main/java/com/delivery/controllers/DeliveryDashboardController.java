package com.delivery.controllers;

import com.delivery.dao.OrderDAO;
import com.delivery.models.DeliveryMan;
import com.delivery.models.Order;
import com.delivery.models.OrderStatus;
import com.delivery.util.AlertUtil;
import com.delivery.util.SessionHolder;

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
        currentDeliveryMan = SessionHolder.getDeliveryMan();
        if (currentDeliveryMan != null) {
            welcomeLabel.setText("Welcome, " + currentDeliveryMan.getUsername());
        }

        colOrderId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getId()).asObject());
        colOrderCustId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getCustomerId()).asObject());
        colOrderStatus.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStatus().name()));
        colOrderAmount.setCellValueFactory(c -> new javafx.beans.property.SimpleDoubleProperty(c.getValue().getTotalAmount()).asObject());

        orderTable.setItems(orderList);
        loadOrders();
    }

    private void loadOrders() {
        if (currentDeliveryMan == null) return;
        orderList.setAll(orderDAO.findByDeliveryManId(currentDeliveryMan.getId()));
    }

    @FXML
    private void onMarkOutForDelivery() {
        Order o = orderTable.getSelectionModel().getSelectedItem();
        if (o == null) {
            AlertUtil.error("Error", "Select an order");
            return;
        }
        orderDAO.updateStatus(o.getId(), OrderStatus.OUT_FOR_DELIVERY);
        loadOrders();
    }

    @FXML
    private void onMarkDelivered() {
        Order o = orderTable.getSelectionModel().getSelectedItem();
        if (o == null) {
            AlertUtil.error("Error", "Select an order");
            return;
        }
        orderDAO.updateStatus(o.getId(), OrderStatus.DELIVERED);
        loadOrders();
    }
}
