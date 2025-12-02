package com.delivery.services;

import com.delivery.dao.AdminDAO;
import com.delivery.dao.CustomerDAO;
import com.delivery.dao.DeliveryManDAO;
import com.delivery.models.Admin;
import com.delivery.models.Customer;
import com.delivery.models.DeliveryMan;

public class AuthService {

    private final AdminDAO adminDAO = new AdminDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final DeliveryManDAO deliveryManDAO = new DeliveryManDAO();

    public enum Role {
        ADMIN,
        CUSTOMER,
        DELIVERY_MAN
    }

    public Admin loginAdmin(String username, String plainPassword) {
        String hash = PasswordUtil.hashPassword(plainPassword);
        return adminDAO.findByUsernameAndPasswordHash(username, hash);
    }

    public Customer loginCustomer(String username, String plainPassword) {
        String hash = PasswordUtil.hashPassword(plainPassword);
        System.out.println(hash);
        return customerDAO.findByUsernameAndPasswordHash(username, hash);
    }

    public DeliveryMan loginDeliveryMan(String username, String plainPassword) {
        String hash = PasswordUtil.hashPassword(plainPassword);
        return deliveryManDAO.findByUsernameAndPasswordHash(username, hash);
    }
}
