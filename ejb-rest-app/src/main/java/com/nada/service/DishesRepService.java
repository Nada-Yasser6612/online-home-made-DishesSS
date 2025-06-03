package com.nada.service;

import com.nada.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import jakarta.ejb.Stateless;


@Stateless
public class DishesRepService {

    public String loginDishesRep(String email, String password) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT u.id AS user_id, u.username, u.password, r.id AS rep_table_id " +
                    "FROM users u " +
                    "JOIN dishes_representative r ON u.id = r.user_id " +
                    "WHERE u.email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                return "{\"status\":\"EMAIL_NOT_FOUND\"}";
            }

            String storedPassword = rs.getString("password");
            if (!storedPassword.equals(password)) {
                return "{\"status\":\"WRONG_PASSWORD\"}";
            }

            int userId = rs.getInt("user_id");
            int repTableId = rs.getInt("rep_table_id");
            String username = rs.getString("username");

            return String.format(
                    "{\"status\":\"success\", \"userId\":%d, \"repTableId\":%d, \"username\":\"%s\"}",
                    userId, repTableId, username.replace("\"", "\\\"")
            );

        } catch (SQLException e) {
            e.printStackTrace();
            return "{\"status\":\"DB_ERROR\"}";
        }
    }

}
