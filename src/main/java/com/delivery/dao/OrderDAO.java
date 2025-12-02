package com.delivery.dao;

import com.delivery.database.Database;
import com.delivery.models.Order;
import com.delivery.models.OrderStatus;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private Order map(ResultSet rs) throws SQLException {
        Integer deliverymanId = null;

        int dmId = rs.getInt("deliveryman_id");
        if (!rs.wasNull()) {
            deliverymanId = dmId;
        }

        return new Order(
                rs.getInt("id"),
                rs.getInt("customer_id"),
                deliverymanId,
                OrderStatus.valueOf(rs.getString("status")),
                rs.getDouble("total_amount"),
                LocalDateTime.parse(rs.getString("created_at"), FMT),
                LocalDateTime.parse(rs.getString("updated_at"), FMT)
        );
    }

    public List<Order> findAll() {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Order> findByDeliveryManId(int deliveryManId) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE deliveryman_id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, deliveryManId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void createSimpleOrder(int customerId, double amount) {
        String now = LocalDateTime.now().format(FMT);
        String sql = "INSERT INTO orders(customer_id, deliveryman_id, status, total_amount, created_at, updated_at) " +
                     "VALUES(?, NULL, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ps.setString(2, OrderStatus.PENDING.name());
            ps.setDouble(3, amount);
            ps.setString(4, now);
            ps.setString(5, now);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void assignDeliveryMan(int orderId, int deliveryManId) {
        String now = LocalDateTime.now().format(FMT);
        String sql = "UPDATE orders SET deliveryman_id=?, status=?, updated_at=? WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, deliveryManId);
            ps.setString(2, OrderStatus.ASSIGNED.name());
            ps.setString(3, now);
            ps.setInt(4, orderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateStatus(int orderId, OrderStatus status) {
        String now = LocalDateTime.now().format(FMT);
        String sql = "UPDATE orders SET status=?, updated_at=? WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setString(2, now);
            ps.setInt(3, orderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
