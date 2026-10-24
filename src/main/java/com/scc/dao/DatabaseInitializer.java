package com.scc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseInitializer {

    private DatabaseInitializer() {
    }

    public static void initialize() {

        try (Connection connection =
                     DatabaseConnection.getConnection();
             Statement statement =
                     connection.createStatement()) {

            // ==========================================
            // USERS
            // ==========================================
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                        user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        username TEXT NOT NULL UNIQUE,
                        password TEXT NOT NULL,
                        role TEXT NOT NULL,
                        status TEXT NOT NULL DEFAULT 'PENDING'
                    )
                    """);

            // ==========================================
            // ASSISTANCE TYPES
            // ==========================================
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS assistance_types (
                        assistance_type_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        type_name TEXT NOT NULL UNIQUE,
                        description TEXT,
                        requirements TEXT,
                        type_status TEXT NOT NULL DEFAULT 'ACTIVE'
                    )
                    """);

            // ==========================================
            // ASSISTANCE REQUESTS
            // ==========================================
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS assistance_requests (
                        request_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        applicant_name TEXT NOT NULL,
                        applicant_address TEXT NOT NULL,
                        contact_number TEXT,
                        assistance_type_id INTEGER NOT NULL,
                        request_description TEXT,
                        date_submitted TIMESTAMP
                            NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        verification_status TEXT
                            NOT NULL DEFAULT 'PENDING',
                        verification_remarks TEXT,
                        approval_status TEXT
                            NOT NULL DEFAULT 'PENDING',
                        approval_remarks TEXT,
                        received_by INTEGER,
                        reviewed_by INTEGER,
                        approved_by INTEGER,

                        FOREIGN KEY (assistance_type_id)
                            REFERENCES assistance_types(
                                assistance_type_id
                            ),

                        FOREIGN KEY (received_by)
                            REFERENCES users(user_id),

                        FOREIGN KEY (reviewed_by)
                            REFERENCES users(user_id),

                        FOREIGN KEY (approved_by)
                            REFERENCES users(user_id)
                    )
                    """);

            // ==========================================
            // ACTIVITY LOGS
            // ==========================================
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS activity_logs (
                        log_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        user_id INTEGER NOT NULL,
                        action_performed TEXT NOT NULL,
                        record_id INTEGER,
                        activity_details TEXT,
                        activity_datetime TIMESTAMP
                            NOT NULL DEFAULT CURRENT_TIMESTAMP,

                        FOREIGN KEY (user_id)
                            REFERENCES users(user_id)
                    )
                    """);

            // ==========================================
            // INDEXES
            // ==========================================
            statement.execute("""
                    CREATE INDEX IF NOT EXISTS idx_users_status
                    ON users(status)
                    """);

            statement.execute("""
                    CREATE INDEX IF NOT EXISTS
                    idx_requests_verification_status
                    ON assistance_requests(
                        verification_status
                    )
                    """);

            statement.execute("""
                    CREATE INDEX IF NOT EXISTS
                    idx_requests_approval_status
                    ON assistance_requests(
                        approval_status
                    )
                    """);

            // ==========================================
            // DEFAULT ADMIN
            // ==========================================
            insertDefaultAdmin(connection);

            // ==========================================
            // DEFAULT ASSISTANCE TYPES
            // ==========================================
            insertDefaultAssistanceTypes(connection);

            System.out.println(
                    "Database initialized successfully."
            );

        } catch (SQLException e) {

            System.out.println(
                    "Database initialization failed: "
                            + e.getMessage()
            );

            throw new RuntimeException(e);
        }
    }

    private static void insertDefaultAdmin(
            Connection connection) throws SQLException {

        String sql = """
                INSERT OR IGNORE INTO users
                (username, password, role, status)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, "admin");
            statement.setString(2, "admin123");
            statement.setString(3, "ADMIN");
            statement.setString(4, "APPROVED");

            statement.executeUpdate();
        }
    }

    private static void insertDefaultAssistanceTypes(
            Connection connection) throws SQLException {

        String sql = """
                INSERT OR IGNORE INTO assistance_types
                (type_name, description, requirements, type_status)
                VALUES (?, ?, ?, 'ACTIVE')
                """;

        String[][] types = {
                {
                        "Medical Assistance",
                        "Support for medical expenses",
                        "Medical certificate or hospital document"
                },
                {
                        "Emergency Assistance",
                        "Immediate support for emergency situations",
                        "Valid identification and supporting documents"
                },
                {
                        "Financial Assistance",
                        "Financial support for qualified applicants",
                        "Valid identification and proof of need"
                },
                {
                        "Other Community Support",
                        "Other available community assistance",
                        "Valid identification and supporting documents"
                }
        };

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            for (String[] type : types) {

                statement.setString(1, type[0]);
                statement.setString(2, type[1]);
                statement.setString(3, type[2]);

                statement.executeUpdate();
            }
        }
    }
}
