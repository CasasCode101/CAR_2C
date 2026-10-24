package com.scc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LogDao {

    public static void logAction(int userId, String username, String action,
                                 Integer recordId, String details) {
        String sql = """
            INSERT INTO activity_logs
            (user_id, username, action, record_id, activity_details)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, username);
            pstmt.setString(3, action);

            if (recordId == null) {
                pstmt.setNull(4, java.sql.Types.INTEGER);
            } else {
                pstmt.setInt(4, recordId);
            }

            pstmt.setString(5, details);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Log Error: " + e.getMessage());
        }
    }

    public static void viewAllLogs() {
        String sql = """
            SELECT timestamp, username, action, record_id, activity_details
            FROM activity_logs
            ORDER BY id DESC
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("\n--- USER ACTIVITY LOG / AUDIT TRAIL ---");

            while (rs.next()) {
                System.out.printf(
                        "[%s] User: %s | Action: %s | Request ID: %s | Details: %s%n",
                        rs.getString("timestamp"),
                        rs.getString("username"),
                        rs.getString("action"),
                        rs.getObject("record_id"),
                        rs.getString("activity_details")
                );
            }

        } catch (SQLException e) {
            System.err.println("Database Error: " + e.getMessage());
        }
    }
}
