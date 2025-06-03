package com.nada;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/test_app";
    private static final String USER = "root";
    private static final String PASSWORD = "Nada@1234"; // replace with yours

    public static Connection getConnection() throws SQLException {
        Connection conn =DriverManager.getConnection(URL , USER , PASSWORD);
        return conn;
    }

    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println(" Connected successfully!");
        } catch (SQLException e) {
            System.out.println(" Connection failed:");
            e.printStackTrace();
        }
    }
}
