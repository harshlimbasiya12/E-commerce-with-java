
package com.ecommerce.dao;

import com.ecommerce.model.Order;
import com.ecommerce.util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.ecommerce.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;


public class OrderDAO {

    // ==========================================
    // 1. PLACE ORDER
    // ==========================================

    public boolean placeOrder(Order order) {

        String sql =
            "INSERT INTO orders " +
            "(customer_id, product_id, quantity, total_price) " +
            "VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                return false;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, order.getCustomerId());
                ps.setInt(2, order.getProductId());
                ps.setInt(3, order.getQuantity());
                ps.setDouble(4, order.getTotalPrice());

                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {

            System.out.println("Error placing order!");
            e.printStackTrace();
        }

        return false;
    }


    // ==========================================
    // 2. GET ALL ORDERS
    // ==========================================

    public List<Order> getAllOrders() {

        List<Order> orders = new ArrayList<>();

        String sql =
            "SELECT " +
            "o.order_id, " +
            "o.customer_id, " +
            "o.product_id, " +
            "o.quantity, " +
            "o.total_price, " +
            "o.order_date, " +
            "u.name AS customer_name, " +
            "p.title AS product_title " +

            "FROM orders o " +

            "JOIN users u " +
            "ON o.customer_id = u.user_id " +

            "JOIN products p " +
            "ON o.product_id = p.product_id " +

            "ORDER BY o.order_date DESC";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                return orders;
            }

            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Order order = mapOrder(rs);

                    orders.add(order);
                }
            }

        } catch (SQLException e) {

            System.out.println("Error retrieving all orders!");
            e.printStackTrace();
        }

        return orders;
    }


    // ==========================================
    // 3. GET ORDERS BY CUSTOMER ID
    // ==========================================

    public List<Order> getOrdersByCustomer(int customerId) {

        List<Order> orders = new ArrayList<>();

        String sql =
            "SELECT " +
            "o.order_id, " +
            "o.customer_id, " +
            "o.product_id, " +
            "o.quantity, " +
            "o.total_price, " +
            "o.order_date, " +
            "u.name AS customer_name, " +
            "p.title AS product_title " +

            "FROM orders o " +

            "JOIN users u " +
            "ON o.customer_id = u.user_id " +

            "JOIN products p " +
            "ON o.product_id = p.product_id " +

            "WHERE o.customer_id = ? " +

            "ORDER BY o.order_date DESC";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                return orders;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, customerId);

                try (ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {

                        Order order = mapOrder(rs);

                        orders.add(order);
                    }
                }
            }

        } catch (SQLException e) {

            System.out.println("Error retrieving customer orders!");
            e.printStackTrace();
        }

        return orders;
    }


    // ==========================================
    // 4. MAP RESULTSET TO ORDER OBJECT
    // ==========================================

    private Order mapOrder(ResultSet rs) throws SQLException {

        Order order = new Order();

        order.setOrderId(rs.getInt("order_id"));
        order.setCustomerId(rs.getInt("customer_id"));
        order.setProductId(rs.getInt("product_id"));

        order.setQuantity(rs.getInt("quantity"));

        order.setTotalPrice(rs.getDouble("total_price"));

        order.setOrderDate(
            rs.getTimestamp("order_date")
        );

        order.setCustomerName(
            rs.getString("customer_name")
        );

        order.setProductTitle(
            rs.getString("product_title")
        );

        return order;
    }
    public boolean placeCartOrder(int customerId, Map<Integer, Integer> cart) {

        if (cart == null || cart.isEmpty()) {
            return false;
        }

        String productSQL =
            "SELECT price FROM products WHERE product_id = ? FOR UPDATE";

        String orderSQL =
            "INSERT INTO orders " +
            "(customer_id, product_id, quantity, total_price) " +
            "VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try (
                PreparedStatement productStmt =
                    con.prepareStatement(productSQL);

                PreparedStatement orderStmt =
                    con.prepareStatement(orderSQL)
            ) {

                for (Map.Entry<Integer, Integer> item : cart.entrySet()) {

                    int productId = item.getKey();
                    int quantity = item.getValue();

                    if (quantity < 1) {
                        throw new SQLException("Invalid quantity");
                    }

                    productStmt.setInt(1, productId);

                    BigDecimal price;

                    try (ResultSet rs = productStmt.executeQuery()) {

                        if (!rs.next()) {
                            throw new SQLException("Product no longer exists");
                        }

                        price = rs.getBigDecimal("price");
                    }

                    BigDecimal total =
                        price.multiply(BigDecimal.valueOf(quantity));

                    orderStmt.setInt(1, customerId);
                    orderStmt.setInt(2, productId);
                    orderStmt.setInt(3, quantity);
                    orderStmt.setBigDecimal(4, total);

                    orderStmt.executeUpdate();
                }

                con.commit();
                return true;

            } catch (SQLException ex) {

                con.rollback();
                ex.printStackTrace();
                return false;

            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}