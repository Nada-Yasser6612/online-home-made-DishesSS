package com.nada.service;

import com.nada.DBConnection;
import com.nada.dto.UserDetailsDTO;
import jakarta.ejb.Stateless;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
@Stateless
public class UserService {

    public UserDetailsDTO getUserDetailsById(int userId) {
        UserDetailsDTO dto = null;

        try (Connection conn = DBConnection.getConnection()) {
            // 1. Get base user info
            String userSql = "SELECT * FROM users WHERE id = ?";
            PreparedStatement userStmt = conn.prepareStatement(userSql);
            userStmt.setInt(1, userId);
            ResultSet userRs = userStmt.executeQuery();

            if (!userRs.next()) return null;

            dto = new UserDetailsDTO();
            dto.setId(userRs.getInt("id"));
            dto.setEmail(userRs.getString("email"));
            dto.setUsername(userRs.getString("username"));
            dto.setIsAdmin(userRs.getBoolean("is_admin"));
            dto.setAddress(userRs.getString("address"));
            dto.setPhone(userRs.getString("phone"));

            // 2. Determine role
            if (dto.isIsAdmin()) {
                dto.setRole("admin");
            } else {
                // Check if user is a customer
                String custSql = "SELECT * FROM customer WHERE user_id = ?";
                PreparedStatement custStmt = conn.prepareStatement(custSql);
                custStmt.setInt(1, userId);
                ResultSet custRs = custStmt.executeQuery();
                if (custRs.next()) {
                    dto.setRole("customer");
                    dto.setCardNumber(custRs.getString("card_number"));
                    dto.setExpireDate(custRs.getString("expire_date"));
                }

                // Check if user is a dishes rep
                String repSql = "SELECT * FROM dishes_representative WHERE user_id = ?";
                PreparedStatement repStmt = conn.prepareStatement(repSql);
                repStmt.setInt(1, userId);
                ResultSet repRs = repStmt.executeQuery();
                if (repRs.next()) {
                    dto.setRole("dishesRep");
                    dto.setMinimumCharge(repRs.getLong("minimum_charge"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dto;
    }
}
