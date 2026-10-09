
package com.ecommerce.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // Database configuration
    private static final String URL =
        "jdbc:mysql://localhost:3306/ecommerce_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    private static final String USERNAME = "root";

    private static final String PASSWORD = "Harsh@1212";

    // Establish database connection
    public static Connection getConnection() {

        Connection connection = null;

        try {

            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Create database connection
            connection = DriverManager.getConnection(
                URL,
                USERNAME,
                PASSWORD
            );

        } catch (ClassNotFoundException e) {

            System.out.println("MySQL JDBC Driver not found!");
            e.printStackTrace();

        } catch (SQLException e) {

            System.out.println("Database connection failed!");
            e.printStackTrace();

        }

        return connection;
    }
}