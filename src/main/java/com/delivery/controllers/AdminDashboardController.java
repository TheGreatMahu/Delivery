package com.delivery.controllers;

import com.delivery.dao.DeliveryManDAO;
import com.delivery.dao.OrderDAO;
import com.delivery.dao.ProductDAO;
import com.delivery.models.DeliveryMan;
import com.delivery.models.Order;
import com.delivery.models.OrderStatus;
import com.delivery.models.Product;
import com.delivery.util.AlertUtil;
import com.delivery.util.SessionHolder;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

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
        // Product table bindings
        colProdId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()).asObject());
        colProdTitle.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitle()));
        colProdDesc.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescription()));
        colProdPrice.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getPrice()).asObject());
        colProdStock.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getStock()).asObject());
        productTable.setItems(productList);

        // Orders table bindings
        colOrderId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()).asObject());
        colOrderCustId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getCustomerId()).asObject());
        colOrderStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus().name()));
        colOrderAmount.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getTotalAmount()).asObject());
        orderTable.setItems(orderList);

        // Delivery men combo box
        try {
            deliveryMen.addAll(deliveryManDAO.findAll());
            deliveryManComboBox.setItems(deliveryMen);
        } catch (Exception e) {
            AlertUtil.error("Error", "Failed to load delivery men: " + e.getMessage());
            e.printStackTrace();
        }

        loadProducts();
        loadOrders();
    }

    private void loadProducts() {
        try {
            productList.setAll(productDAO.findAll());
        } catch (Exception e) {
            AlertUtil.error("Error", "Failed to load products: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadOrders() {
        try {
            orderList.setAll(orderDAO.findAll());
        } catch (Exception e) {
            AlertUtil.error("Error", "Failed to load orders: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onAddProduct() {
        try {
            String title = titleField.getText().trim();
            String desc = descriptionField.getText().trim();
            String priceText = priceField.getText().trim();
            String stockText = stockField.getText().trim();

            // Validation
            if (title.isEmpty()) {
                AlertUtil.error("Validation Error", "Title is required");
                return;
            }
            if (desc.isEmpty()) {
                AlertUtil.error("Validation Error", "Description is required");
                return;
            }
            if (priceText.isEmpty() || stockText.isEmpty()) {
                AlertUtil.error("Validation Error", "Price and stock are required");
                return;
            }

            double price = Double.parseDouble(priceText);
            int stock = Integer.parseInt(stockText);

            if (price < 0) {
                AlertUtil.error("Validation Error", "Price cannot be negative");
                return;
            }
            if (stock < 0) {
                AlertUtil.error("Validation Error", "Stock cannot be negative");
                return;
            }

            Product newProduct = new Product(0, title, desc, price, stock);
            productDAO.insert(newProduct);
            AlertUtil.info("Success", "Product added successfully");
            loadProducts();
            clearProductForm();
        } catch (NumberFormatException e) {
            AlertUtil.error("Error", "Invalid price or stock format. Please enter valid numbers.");
        } catch (Exception e) {
            AlertUtil.error("Error", "Failed to add product: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onUpdateProduct() {
        Product p = productTable.getSelectionModel().getSelectedItem();
        if (p == null) {
            AlertUtil.error("Error", "Please select a product to update");
            return;
        }

        try {
            String title = titleField.getText().trim();
            String desc = descriptionField.getText().trim();
            String priceText = priceField.getText().trim();
            String stockText = stockField.getText().trim();

            // Validation
            if (title.isEmpty() || desc.isEmpty()) {
                AlertUtil.error("Validation Error", "Title and description are required");
                return;
            }

            double price = Double.parseDouble(priceText);
            int stock = Integer.parseInt(stockText);

            if (price < 0 || stock < 0) {
                AlertUtil.error("Validation Error", "Price and stock cannot be negative");
                return;
            }

            p.setTitle(title);
            p.setDescription(desc);
            p.setPrice(price);
            p.setStock(stock);

            productDAO.update(p);
            AlertUtil.info("Success", "Product updated successfully");
            loadProducts();
            clearProductForm();
        } catch (NumberFormatException e) {
            AlertUtil.error("Error", "Invalid price or stock format. Please enter valid numbers.");
        } catch (Exception e) {
            AlertUtil.error("Error", "Failed to update product: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onDeleteProduct() {
        Product p = productTable.getSelectionModel().getSelectedItem();
        if (p == null) {
            AlertUtil.error("Error", "Please select a product to delete");
            return;
        }

        try {
            productDAO.delete(p.getId());
            AlertUtil.info("Success", "Product deleted successfully");
            loadProducts();
            clearProductForm();
        } catch (Exception e) {
            AlertUtil.error("Error", "Failed to delete product: " + e.getMessage());
            e.printStackTrace();
        }
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
    private void onLogout() {
        try {
            SessionHolder.clearSession();
            com.delivery.App.setRoot("Login", "/fxml/login.fxml");
        } catch (Exception e) {
            AlertUtil.error("Error", "Failed to logout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onAssignDeliveryMan() {
        Order o = orderTable.getSelectionModel().getSelectedItem();
        DeliveryMan d = deliveryManComboBox.getSelectionModel().getSelectedItem();

        if (o == null) {
            AlertUtil.error("Error", "Please select an order");
            return;
        }
        if (d == null) {
            AlertUtil.error("Error", "Please select a delivery man");
            return;
        }

        try {
            orderDAO.assignDeliveryMan(o.getId(), d.getId());
            AlertUtil.info("Success", "Delivery man assigned successfully");
            loadOrders();
        } catch (Exception e) {
            AlertUtil.error("Error", "Failed to assign delivery man: " + e.getMessage());
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

        try {
            orderDAO.updateStatus(o.getId(), OrderStatus.DELIVERED);
            AlertUtil.info("Success", "Order marked as delivered");
            loadOrders();
        } catch (Exception e) {
            AlertUtil.error("Error", "Failed to update order status: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearProductForm() {
        titleField.clear();
        descriptionField.clear();
        priceField.clear();
        stockField.clear();
        productTable.getSelectionModel().clearSelection();
    }
}