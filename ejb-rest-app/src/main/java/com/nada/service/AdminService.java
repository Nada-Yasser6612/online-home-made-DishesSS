package com.nada.service;
import com.nada.model.Customer;
import com.nada.model.User;
import com.nada.model.DishesRepresentative;


import com.nada.util.PasswordGenerator;
import com.nada.DBConnection;
import jakarta.ejb.Stateless;


import java.sql.*;
import java.util.*;

@Stateless
public class AdminService {
    private boolean isCompanyNameUnique(Connection conn, String name) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public Map<String, String> createRepAccounts(List<String> companyNames) {
        Map<String, String> result = new HashMap<>();

        try (Connection conn = DBConnection.getConnection()) {
            for (String company : companyNames) {
                // Check uniqueness
                if (!isCompanyNameUnique(conn , company)) {
                    result.put(company, "Duplicate - skipped");
                    continue;
                }

                String password = PasswordGenerator.generate();

                // 1. Insert into users
                String userSQL = "INSERT INTO users (email, password, username, is_admin, address, phone) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement userStmt = conn.prepareStatement(userSQL, Statement.RETURN_GENERATED_KEYS);
                userStmt.setString(1, company + "@company.com"); // dummy email
                userStmt.setString(2, password);
                userStmt.setString(3, company); // username
                userStmt.setBoolean(4, false); // is_admin
                userStmt.setString(5, "N/A");
                userStmt.setString(6, "0000");

                int affected = userStmt.executeUpdate();

                if (affected > 0) {
                    ResultSet rs = userStmt.getGeneratedKeys();
                    if (rs.next()) {
                        int userId = rs.getInt(1);

                        // 2. Insert into dishes_representative
                        String repSQL = "INSERT INTO dishes_representative (user_id, minimum_charge) VALUES (?, ?)";
                        PreparedStatement repStmt = conn.prepareStatement(repSQL);
                        repStmt.setInt(1, userId);
                        repStmt.setLong(2, 0);
                        repStmt.executeUpdate();

                        result.put(company, password);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public boolean isAdmin(int userId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT is_admin FROM users WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getBoolean("is_admin");
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String loginAdmin(String email, String password) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                return "{\"status\":\"EMAIL_NOT_FOUND\"}";
            }

            String storedPassword = rs.getString("password");
            boolean isAdmin = rs.getBoolean("is_admin");

            if (!isAdmin) {
                return "{\"status\":\"NOT_ADMIN\"}";
            }

            if (!storedPassword.equals(password)) {
                return "{\"status\":\"WRONG_PASSWORD\"}";
            }

            int userId = rs.getInt("id");
            String username = rs.getString("username");

            return String.format(
                    "{\"status\":\"success\", \"userId\":%d, \"username\":\"%s\"}",
                    userId, username.replace("\"", "\\\"")
            );

        } catch (SQLException e) {
            e.printStackTrace();
            return "{\"status\":\"DB_ERROR\"}";
        }
    }



    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = """
        SELECT u.id AS user_id, u.username, u.email, u.phone, u.address,
               c.id AS customer_id, c.card_number, c.expire_date
        FROM users u
        JOIN customer c ON u.id = c.user_id
    """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Customer customer = new Customer();
                customer.setId(rs.getInt("user_id"));
                customer.setUsername(rs.getString("username"));
                customer.setEmail(rs.getString("email"));
                customer.setPhone(rs.getString("phone"));
                customer.setAddress(rs.getString("address"));
                customer.setCustomerId(rs.getInt("customer_id"));
                customer.setCardNumber(rs.getString("card_number"));
                customer.setExpireDate(rs.getString("expire_date"));
                customers.add(customer);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return customers;
    }
    public List<DishesRepresentative> getAllDishesReps() {
        List<DishesRepresentative> reps = new ArrayList<>();

        String sql = "SELECT dr.id AS rep_id, dr.minimum_charge, u.* " +
                "FROM dishes_representative dr " +
                "JOIN users u ON dr.user_id = u.id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                DishesRepresentative rep = new DishesRepresentative(
                        rs.getInt("rep_id"),
                        rs.getLong("minimum_charge"),
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getBoolean("is_admin"),
                        rs.getString("address"),
                        rs.getString("phone")
                );
                reps.add(rep);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reps;
    }




}
