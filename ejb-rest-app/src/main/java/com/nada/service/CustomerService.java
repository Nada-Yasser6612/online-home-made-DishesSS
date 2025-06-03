package com.nada.service;

import com.nada.model.Customer;
import com.nada.DBConnection;
import jakarta.ejb.Stateless;

import java.sql.*;

@Stateless
public class CustomerService {

    public String registerCustomer(Customer customer) {
        try (Connection conn = DBConnection.getConnection()) {
            String userSQL = "INSERT INTO users (email, password, username, is_admin, address, phone) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement userStmt = conn.prepareStatement(userSQL, Statement.RETURN_GENERATED_KEYS);
            userStmt.setString(1, customer.getEmail());
            userStmt.setString(2, customer.getPassword());
            userStmt.setString(3, customer.getUsername());
            userStmt.setBoolean(4, false); // Not admin
            userStmt.setString(5, customer.getAddress());
            userStmt.setString(6, customer.getPhone());

            int affected = userStmt.executeUpdate();
            if (affected == 0) {
                return "Failed to insert into users table.";
            }

            ResultSet rs = userStmt.getGeneratedKeys();
            if (rs.next()) {
                int userId = rs.getInt(1);
                customer.setId(userId); // Set inherited ID
                String custSQL = "INSERT INTO customer (user_id, card_number, expire_date) VALUES (?, ?, ?)";
                PreparedStatement custStmt = conn.prepareStatement(custSQL);
                custStmt.setInt(1, userId);
                custStmt.setString(2, customer.getCardNumber());
                custStmt.setString(3, customer.getExpireDate());

                custStmt.executeUpdate();
                return "Customer registered successfully!";
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            return "Email already exists.";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Database error: " + e.getMessage();
        }

        return "Unknown error during registration.";
    }public String loginCustomer(String email, String password) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM users u JOIN customer c ON u.id = c.user_id WHERE u.email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                return "EMAIL_NOT_FOUND";
            }

            String storedPassword = rs.getString("password");
            if (!storedPassword.equals(password)) {
                return "WRONG_PASSWORD";
            }

            int userId = rs.getInt("id");
            String username = rs.getString("username");

            return "SUCCESS:" + userId + ":" + username;

        } catch (SQLException e) {
            e.printStackTrace();
            return "DB_ERROR";
        }
    }


}
