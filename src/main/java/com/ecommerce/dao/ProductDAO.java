
package com.ecommerce.dao;

import com.ecommerce.model.Product;
import com.ecommerce.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    // ==========================================
    // 1. ADD PRODUCT
    // ==========================================

    public boolean addProduct(Product product) {

        String sql =
            "INSERT INTO products " +
            "(owner_id, title, price, description, image_url) " +
            "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                return false;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, product.getOwnerId());
                ps.setString(2, product.getTitle());
                ps.setDouble(3, product.getPrice());
                ps.setString(4, product.getDescription());
                ps.setString(5, product.getImageUrl());

                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {

            System.out.println("Error adding product!");
            e.printStackTrace();
        }

        return false;
    }


    // ==========================================
    // 2. GET ALL PRODUCTS
    // ==========================================

    public List<Product> getAllProducts() {

        List<Product> products = new ArrayList<>();

        String sql =
            "SELECT * FROM products ORDER BY created_at DESC";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                return products;
            }

            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Product product = mapProduct(rs);

                    products.add(product);
                }
            }

        } catch (SQLException e) {

            System.out.println("Error retrieving products!");
            e.printStackTrace();
        }

        return products;
    }


    // ==========================================
    // 3. GET PRODUCT BY ID
    // ==========================================

    public Product getProductById(int productId) {

        String sql =
            "SELECT * FROM products WHERE product_id = ?";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                return null;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, productId);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {

                        return mapProduct(rs);
                    }
                }
            }

        } catch (SQLException e) {

            System.out.println("Error retrieving product!");
            e.printStackTrace();
        }

        return null;
    }


    // ==========================================
    // 4. GET PRODUCTS BY OWNER ID
    // ==========================================

    public List<Product> getProductsByOwner(int ownerId) {

        List<Product> products = new ArrayList<>();

        String sql =
            "SELECT * FROM products " +
            "WHERE owner_id = ? " +
            "ORDER BY created_at DESC";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                return products;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, ownerId);

                try (ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {

                        Product product = mapProduct(rs);

                        products.add(product);
                    }
                }
            }

        } catch (SQLException e) {

            System.out.println("Error retrieving owner products!");
            e.printStackTrace();
        }

        return products;
    }


    // ==========================================
    // 5. UPDATE PRODUCT
    // ==========================================

    public boolean updateProduct(Product product) {

        String sql =
            "UPDATE products SET " +
            "title = ?, price = ?, description = ?, image_url = ? " +
            "WHERE product_id = ? AND owner_id = ?";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                return false;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, product.getTitle());
                ps.setDouble(2, product.getPrice());
                ps.setString(3, product.getDescription());
                ps.setString(4, product.getImageUrl());

                ps.setInt(5, product.getProductId());
                ps.setInt(6, product.getOwnerId());

                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {

            System.out.println("Error updating product!");
            e.printStackTrace();
        }

        return false;
    }


    // ==========================================
    // 6. DELETE PRODUCT
    // ==========================================

    public boolean deleteProduct(int productId, int ownerId) {

        String sql =
            "DELETE FROM products " +
            "WHERE product_id = ? AND owner_id = ?";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                return false;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, productId);
                ps.setInt(2, ownerId);

                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {

            System.out.println("Error deleting product!");
            System.out.println(
                "The product may already be associated with an order."
            );

            e.printStackTrace();
        }

        return false;
    }


    // ==========================================
    // 7. MAP RESULTSET TO PRODUCT OBJECT
    // ==========================================

    private Product mapProduct(ResultSet rs) throws SQLException {

        Product product = new Product();

        product.setProductId(rs.getInt("product_id"));
        product.setOwnerId(rs.getInt("owner_id"));
        product.setTitle(rs.getString("title"));
        product.setPrice(rs.getDouble("price"));
        product.setDescription(rs.getString("description"));
        product.setImageUrl(rs.getString("image_url"));
        product.setCreatedAt(rs.getTimestamp("created_at"));

        return product;
    }
}