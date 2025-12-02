package com.delivery.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.delivery.database.Database;
import com.delivery.models.Customer;

public class CustomerDAO {

    public Customer findByUsernameAndPasswordHash(String username, String passwordHash) {
        String sql = "SELECT id, username, password FROM customers WHERE username=? AND password=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println(rs.getString("password"));
                System.out.println(rs.getString("username"));
                return new Customer(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password")
                );
            }
            else {
                System.err.println("Nigga");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Customer findById(int id) {
        String sql = "SELECT id, username, password FROM customers WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Customer(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
