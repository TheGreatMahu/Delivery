package com.delivery.dao;

import com.delivery.database.Database;
import com.delivery.models.DeliveryMan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DeliveryManDAO {

    public DeliveryMan findByUsernameAndPasswordHash(String username, String passwordHash) {
        String sql = "SELECT id, username, password FROM deliverymen WHERE username=? AND password=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new DeliveryMan(
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

    public List<DeliveryMan> findAll() {
        List<DeliveryMan> list = new ArrayList<>();
        String sql = "SELECT id, username, password FROM deliverymen";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new DeliveryMan(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
