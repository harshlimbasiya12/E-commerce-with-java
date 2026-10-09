
package com.ecommerce.dao;

import com.ecommerce.model.User;
import com.ecommerce.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.security.MessageDigest;
import java.security.SecureRandom;

import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class UserDAO {

    private static final int ITERATIONS = 120000;
    private static final int KEY_LENGTH = 256;

    private static final SecureRandom RANDOM = new SecureRandom();

    // ==========================================
    // 1. USER LOGIN AUTHENTICATION
    // ==========================================

    public User authenticate(String email, String password) {

        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                return null;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, email);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {

                        String storedPassword = rs.getString("password");

                        boolean validPassword;

                        // Check whether password is already hashed
                        if (storedPassword.startsWith("PBKDF2$")) {

                            validPassword = verifyPassword(
                                password,
                                storedPassword
                            );

                        } else {

                            // Temporary compatibility for Phase 1
                            // dummy accounts only
                            validPassword = MessageDigest.isEqual(
                                password.getBytes(
                                    java.nio.charset.StandardCharsets.UTF_8
                                ),
                                storedPassword.getBytes(
                                    java.nio.charset.StandardCharsets.UTF_8
                                )
                            );

                            // Upgrade legacy password after successful login
                            if (validPassword) {

                                String hashedPassword =
                                    hashPassword(password);

                                updatePasswordHash(
                                    con,
                                    rs.getInt("user_id"),
                                    hashedPassword
                                );
                            }
                        }

                        if (validPassword) {

                            User user = new User();

                            user.setUserId(rs.getInt("user_id"));
                            user.setName(rs.getString("name"));
                            user.setEmail(rs.getString("email"));
                            user.setRole(rs.getString("role"));
                            user.setCreatedAt(
                                rs.getTimestamp("created_at")
                            );

                            // Never return password to the controller
                            user.setPassword(null);

                            return user;
                        }
                    }
                }

            }

        } catch (Exception e) {

            System.out.println("Login authentication error!");
            e.printStackTrace();
        }

        return null;
    }


    // ==========================================
    // 2. GET USER BY ID
    // ==========================================

    public User getUserById(int userId) {

        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                return null;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, userId);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {

                        User user = new User();

                        user.setUserId(rs.getInt("user_id"));
                        user.setName(rs.getString("name"));
                        user.setEmail(rs.getString("email"));
                        user.setRole(rs.getString("role"));
                        user.setCreatedAt(
                            rs.getTimestamp("created_at")
                        );

                        user.setPassword(null);

                        return user;
                    }
                }
            }

        } catch (SQLException e) {

            System.out.println("Error retrieving user!");
            e.printStackTrace();
        }

        return null;
    }


    // ==========================================
    // 3. HASH PASSWORD USING PBKDF2
    // ==========================================

    public static String hashPassword(String password)
            throws Exception {

        byte[] salt = new byte[16];

        RANDOM.nextBytes(salt);

        PBEKeySpec spec = new PBEKeySpec(
            password.toCharArray(),
            salt,
            ITERATIONS,
            KEY_LENGTH
        );

        try {

            SecretKeyFactory factory =
                SecretKeyFactory.getInstance(
                    "PBKDF2WithHmacSHA256"
                );

            byte[] hash = factory.generateSecret(spec).getEncoded();

            return "PBKDF2$" +
                ITERATIONS + "$" +
                Base64.getEncoder().encodeToString(salt) + "$" +
                Base64.getEncoder().encodeToString(hash);

        } finally {

            spec.clearPassword();
        }
    }


    // ==========================================
    // 4. VERIFY HASHED PASSWORD
    // ==========================================

    private boolean verifyPassword(
            String password,
            String storedPassword) throws Exception {

        String[] parts = storedPassword.split("\\$");

        if (parts.length != 4 || !parts[0].equals("PBKDF2")) {
            return false;
        }

        int iterations = Integer.parseInt(parts[1]);

        // Reject malformed or unreasonable hash parameters
        if (iterations < 1 || iterations > 1000000) {
            return false;
        }

        byte[] salt = Base64.getDecoder().decode(parts[2]);

        byte[] originalHash = Base64.getDecoder().decode(parts[3]);

        if (salt.length != 16 || originalHash.length != 32) {
            return false;
        }

        PBEKeySpec spec = new PBEKeySpec(
            password.toCharArray(),
            salt,
            iterations,
            originalHash.length * 8
        );

        try {

            SecretKeyFactory factory =
                SecretKeyFactory.getInstance(
                    "PBKDF2WithHmacSHA256"
                );

            byte[] newHash = factory.generateSecret(spec).getEncoded();

            return MessageDigest.isEqual(
                originalHash,
                newHash
            );

        } finally {

            spec.clearPassword();
        }
    }


    // ==========================================
    // 5. UPDATE LEGACY PASSWORD TO HASH
    // ==========================================

    private void updatePasswordHash(
            Connection con,
            int userId,
            String hashedPassword) throws SQLException {

        String sql =
            "UPDATE users SET password = ? WHERE user_id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, hashedPassword);
            ps.setInt(2, userId);

            ps.executeUpdate();
        }
    }
}