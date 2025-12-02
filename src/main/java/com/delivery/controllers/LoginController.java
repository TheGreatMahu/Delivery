package com.delivery.controllers;

import com.delivery.App;
import com.delivery.models.Admin;
import com.delivery.models.Customer;
import com.delivery.models.DeliveryMan;
import com.delivery.services.AuthService;
import com.delivery.util.AlertUtil;
import com.delivery.util.SessionHolder;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;



public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleComboBox;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        roleComboBox.getItems().addAll("Admin", "Customer", "Delivery Man");
        roleComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void onLoginClicked() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String role = roleComboBox.getValue();

        if (username.isEmpty() || password.isEmpty()) {
            AlertUtil.error("Login Failed", "Username and password required");
            return;
        }

        try {
            switch (role) {
                case "Admin" -> {
                    Admin admin = authService.loginAdmin(username, password);
                    if (admin == null) {
                        AlertUtil.error("Login Failed", "Invalid admin credentials");
                        return;
                    }
                    SessionHolder.setAdmin(admin);
                    App.setRoot("Admin Dashboard", "/fxml/admin_dashboard.fxml");
                }
                case "Customer" -> {
                    Customer c = authService.loginCustomer(username, password);
                    if (c == null) {
                        AlertUtil.error("Login Failed", "Invalid customer credentials");
                        return;
                    }
                    SessionHolder.setCustomer(c);
                    App.setRoot("Customer Dashboard", "/fxml/customer_dashboard.fxml");
                }
                case "Delivery Man" -> {
                    DeliveryMan d = authService.loginDeliveryMan(username, password);
                    if (d == null) {
                        AlertUtil.error("Login Failed", "Invalid delivery man credentials");
                        return;
                    }
                    SessionHolder.setDeliveryMan(d);
                    App.setRoot("Delivery Dashboard", "/fxml/delivery_dashboard.fxml");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.error("Error", "Unexpected error: " + e.getMessage());
        }
    }
}
