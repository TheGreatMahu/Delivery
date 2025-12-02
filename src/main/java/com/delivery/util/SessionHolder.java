package com.delivery.util;

import com.delivery.models.Admin;
import com.delivery.models.Customer;
import com.delivery.models.DeliveryMan;

public class SessionHolder {

    private static Admin admin;
    private static Customer customer;
    private static DeliveryMan deliveryMan;

    // Admin session
    public static void setAdmin(Admin a) {
        admin = a;
        customer = null;
        deliveryMan = null;
    }

    public static Admin getAdmin() {
        return admin;
    }

    // Customer session
    public static void setCustomer(Customer c) {
        customer = c;
        admin = null;
        deliveryMan = null;
    }

    public static Customer getCustomer() {
        return customer;
    }

    // Delivery Man session
    public static void setDeliveryMan(DeliveryMan d) {
        deliveryMan = d;
        admin = null;
        customer = null;
    }

    public static DeliveryMan getDeliveryMan() {
        return deliveryMan;
    }

    // Clear session
    public static void clearSession() {
        admin = null;
        customer = null;
        deliveryMan = null;
    }
}
