package com.scc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserDao {
    public UserDao() {
    }

    public boolean registerUser(String username, String password, String role) {
        String sql = "INSERT INTO users(username, password, roles, status) VALUES(?, ?, ?, 'PENDING')";

        try {
            boolean var7;
            try (
                    Connection conn = DatabaseConnection.getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(sql);
            ) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                pstmt.setString(3, role.toUpperCase());
                pstmt.executeUpdate();
                System.out.println("-> Successful Rigestration, Waiting for Approval!");
                var7 = true;
            }

            return var7;
        } catch (SQLException e) {
            System.err.println("Failed: " + e.getMessage());
            return false;
        }
    }
}
