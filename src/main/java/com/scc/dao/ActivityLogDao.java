
package com.scc.dao;

import com.scc.Model.ActivityLog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogDao {

    // RECORD USER ACTIVITY
    public void logAction(
            int userId,
            String actionType,
            int recordId,
            String description) {

        String sql = """
                INSERT INTO activity_logs
                (user_id, action_performed, record_id, activity_details)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, actionType.toUpperCase());
            pstmt.setInt(3, recordId);
            pstmt.setString(4, description);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(
                    "Failed to record activity log: " + e.getMessage());
        }
    }

    // VIEW ALL ACTIVITY LOGS
    public List<ActivityLog> getAllLogs() {

        List<ActivityLog> logs = new ArrayList<>();

        String sql = """
                SELECT log_id, user_id, action_performed,
                       record_id, activity_details, activity_datetime
                FROM activity_logs
                ORDER BY activity_datetime DESC, log_id DESC
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                logs.add(mapLog(rs));
            }

        } catch (SQLException e) {
            System.out.println(
                    "Failed to retrieve activity logs: " + e.getMessage());
        }

        return logs;
    }

    // FILTER LOGS BY USER ID
    public List<ActivityLog> getLogsByUserId(int userId) {

        List<ActivityLog> logs = new ArrayList<>();

        String sql = """
                SELECT log_id, user_id, action_performed,
                       record_id, activity_details, activity_datetime
                FROM activity_logs
                WHERE user_id = ?
                ORDER BY activity_datetime DESC, log_id DESC
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    logs.add(mapLog(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println(
                    "Failed to retrieve user activity logs: " + e.getMessage());
        }

        return logs;
    }

    // CLEAR ALL ACTIVITY LOGS
    public boolean clearAllLogs() {

        String sql = "DELETE FROM activity_logs";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println(
                    "Failed to clear activity logs: " + e.getMessage());
            return false;
        }
    }

    // CONVERT DATABASE RECORD INTO ACTIVITYLOG OBJECT
    private ActivityLog mapLog(ResultSet rs) throws SQLException {

        return new ActivityLog(
                rs.getInt("log_id"),
                rs.getInt("user_id"),
                rs.getString("action_performed"),
                rs.getInt("record_id"),
                rs.getString("activity_details"),
                rs.getString("activity_datetime")
        );
    }
}
