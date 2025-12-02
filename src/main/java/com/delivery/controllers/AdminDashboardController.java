package com.delivery.controllers;

import com.delivery.dao.DeliveryManDAO;
import com.delivery.dao.OrderDAO;
import com.delivery.dao.ProductDAO;
import com.delivery.models.DeliveryMan;
import com.delivery.models.Order;
import com.delivery.models.OrderStatus;
import com.delivery.models.Product;
import com.delivery.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AdminDashboardController {

    // Product section
    @FXML private TextField titleField;
    @FXML private TextField descriptionField;
    @FXML private TextField priceField;
    @FXML private TextField stockField;
    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, Integer> colProdId;
    @FXML private TableColumn<Product, String> colProdTitle;
    @FXML private TableColumn<Product, String> colProdDesc;
    @FXML private TableColumn<Product, Double> colProdPrice;
    @FXML private TableColumn<Product, Integer> colProdStock;

    // Order section
    @FXML private TableView<Order> orderTable;
    @FXML private TableColumn<Order, Integer> colOrderId;
    @FXML private TableColumn<Order, Integer> colOrderCustId;
    @FXML private TableColumn<Order, String> colOrderStatus;
    @FXML private TableColumn<Order, Double> colOrderAmount;

    @FXML private ComboBox<DeliveryMan> deliveryManComboBox;

    private final ProductDAO productDAO = new ProductDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private final DeliveryManDAO deliveryManDAO = new DeliveryManDAO();

    private final ObservableList<Product> productList = FXCollections.observableArrayList();
    private final ObservableList<Order> orderList = FXCollections.observableArrayList();
    private final ObservableList<DeliveryMan> deliveryMen = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Product table
        colProdId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getId()).asObject());
        colProdTitle.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTitle()));
        colProdDesc.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getDescription()));
        colProdPrice.setCellValueFactory(c -> new javafx.beans.property.SimpleDoubleProperty(c.getValue().getPrice()).asObject());
        colProdStock.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getStock()).asObject());
        productTable.setItems(productList);

        // Orders table
        colOrderId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getId()).asObject());
        colOrderCustId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getCustomerId()).asObject());
        colOrderStatus.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStatus().name()));
        colOrderAmount.setCellValueFactory(c -> new javafx.beans.property.SimpleDoubleProperty(c.getValue().getTotalAmount()).asObject());
        orderTable.setItems(orderList);

        // Delivery men
        deliveryMen.addAll(deliveryManDAO.findAll());
        deliveryManComboBox.setItems(deliveryMen);

        loadProducts();
        loadOrders();
    }

    private void loadProducts() {
        productList.setAll(productDAO.findAll());
    }

    private void loadOrders() {
        orderList.setAll(orderDAO.findAll());
    }

    @FXML
    private void onAddProduct() {
        try {
            String title = titleField.getText().trim();
            String desc = descriptionField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            int stock = Integer.parseInt(stockField.getText().trim());

            if (title.isEmpty() || desc.isEmpty()) {
                AlertUtil.error("Error", "Title and description required");
                return;
            }

            productDAO.insert(new Product(0, title, desc, price, stock));
            loadProducts();
            clearProductForm();
        } catch (NumberFormatException e) {
            AlertUtil.error("Error", "Invalid price or stock");
        }
    }

    @FXML
    private void onUpdateProduct() {
        Product p = productTable.getSelectionModel().getSelectedItem();
        if (p == null) {
            AlertUtil.error("Error", "Select a product to update");
            return;
        }
        try {
            p.setTitle(titleField.getText().trim());
            p.setDescription(descriptionField.getText().trim());
            p.setPrice(Double.parseDouble(priceField.getText().trim()));
            p.setStock(Integer.parseInt(stockField.getText().trim()));
            productDAO.update(p);
            loadProducts();
        } catch (NumberFormatException e) {
            AlertUtil.error("Error", "Invalid price or stock");
        }
    }

    @FXML
    private void onDeleteProduct() {
        Product p = productTable.getSelectionModel().getSelectedItem();
        if (p == null) {
            AlertUtil.error("Error", "Select a product to delete");
            return;
        }
        productDAO.delete(p.getId());
        loadProducts();
    }

    @FXML
    private void onProductTableClicked() {
        Product p = productTable.getSelectionModel().getSelectedItem();
        if (p == null) return;
        titleField.setText(p.getTitle());
        descriptionField.setText(p.getDescription());
        priceField.setText(String.valueOf(p.getPrice()));
        stockField.setText(String.valueOf(p.getStock()));
    }

    @FXML
    private void onAssignDeliveryMan() {
        Order o = orderTable.getSelectionModel().getSelectedItem();
        DeliveryMan d = deliveryManComboBox.getSelectionModel().getSelectedItem();

        if (o == null || d == null) {
            AlertUtil.error("Error", "Select an order and a delivery man");
            return;
        }

        orderDAO.assignDeliveryMan(o.getId(), d.getId());
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

    private void clearProductForm() {
        titleField.clear();
        descriptionField.clear();
        priceField.clear();
        stockField.clear();
    }
}
