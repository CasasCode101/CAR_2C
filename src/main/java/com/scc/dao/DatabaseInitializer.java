package com.scc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class DatabaseInitializer {

    private DatabaseInitializer() {
    }

    public static void initialize() {

        String usersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    role TEXT NOT NULL,
                    status TEXT NOT NULL DEFAULT 'PENDING'
                )
                """;

        String assistanceTypesTable = """
                CREATE TABLE IF NOT EXISTS assistance_types (
                    assistance_type_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    type_name TEXT NOT NULL UNIQUE,
                    description TEXT,
                    requirements TEXT,
                    type_status TEXT NOT NULL DEFAULT 'ACTIVE'
                )
                """;

        String requestsTable = """
                CREATE TABLE IF NOT EXISTS assistance_requests (
                    request_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    applicant_name TEXT NOT NULL,
                    applicant_address TEXT NOT NULL,
                    contact_number TEXT,
                    assistance_type_id INTEGER NOT NULL,
                    request_description TEXT,
                    date_submitted TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    verification_status TEXT DEFAULT 'PENDING',
                    verification_remarks TEXT,
                    approval_status TEXT DEFAULT 'PENDING',
                    approval_remarks TEXT,
                    received_by INTEGER,
                    reviewed_by INTEGER,
                    approved_by INTEGER,
                    FOREIGN KEY (assistance_type_id)
                        REFERENCES assistance_types(assistance_type_id),
                    FOREIGN KEY (received_by)
                        REFERENCES users(user_id),
                    FOREIGN KEY (reviewed_by)
                        REFERENCES users(user_id),
                    FOREIGN KEY (approved_by)
                        REFERENCES users(user_id)
                )
                """;

        String activityLogsTable = """
                CREATE TABLE IF NOT EXISTS activity_logs (
                    log_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    action_performed TEXT NOT NULL,
                    record_id INTEGER,
                    activity_details TEXT,
                    activity_datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (user_id)
                        REFERENCES users(user_id)
                )
                """;

        try (Connection conn = DatabaseConnection.getConnection()) {

            executeCreateTable(conn, usersTable);
            executeCreateTable(conn, assistanceTypesTable);
            executeCreateTable(conn, requestsTable);
            executeCreateTable(conn, activityLogsTable);

            System.out.println("Database initialized successfully!");

        } catch (SQLException e) {
            System.out.println("Database Initialization Error: "
                    + e.getMessage());
        }
    }

    private static void executeCreateTable(
            Connection conn,
            String sql) throws SQLException {

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.execute();
        }
    }
}