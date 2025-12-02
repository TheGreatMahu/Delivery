package com.delivery.controllers;

import com.delivery.dao.OrderDAO;
import com.delivery.dao.ProductDAO;
import com.delivery.models.Customer;
import com.delivery.models.Product;
    import com.delivery.util.AlertUtil;
import com.delivery.util.SessionHolder;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;


public class CustomerDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, Integer> colProdId;
    @FXML private TableColumn<Product, String> colProdTitle;
    @FXML private TableColumn<Product, String> colProdDesc;
    @FXML private TableColumn<Product, Double> colProdPrice;
    @FXML private TableColumn<Product, Integer> colProdStock;

    private final ProductDAO productDAO = new ProductDAO();
    private final OrderDAO orderDAO = new OrderDAO();

    private final ObservableList<Product> productList = FXCollections.observableArrayList();
    private Customer currentCustomer;

    @FXML
    public void initialize() {
        currentCustomer = SessionHolder.getCustomer();
        if (currentCustomer != null) {
            welcomeLabel.setText("Welcome, " + currentCustomer.getUsername());
        }

        colProdId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getId()).asObject());
        colProdTitle.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTitle()));
        colProdDesc.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getDescription()));
        colProdPrice.setCellValueFactory(c -> new javafx.beans.property.SimpleDoubleProperty(c.getValue().getPrice()).asObject());
        colProdStock.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getStock()).asObject());

        productTable.setItems(productList);
        loadProducts();
    }

    private void loadProducts() {
        productList.setAll(productDAO.findAll());
    }

    @FXML
    private void onPlaceOrder() {
        Product p = productTable.getSelectionModel().getSelectedItem();
        if (p == null) {
            AlertUtil.error("Error", "Select a product");
            return;
        }
        if (currentCustomer == null) {
            AlertUtil.error("Error", "No customer session");
            return;
        }
        // simple: 1 unit order
        orderDAO.createSimpleOrder(currentCustomer.getId(), p.getPrice());
        AlertUtil.info("Success", "Order placed successfully!");
    }
}
