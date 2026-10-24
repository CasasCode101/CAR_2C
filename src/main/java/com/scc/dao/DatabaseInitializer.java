package com.scc.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public final class DatabaseInitializer {

    private DatabaseInitializer() {
    }

    public static void initialize() {
        String usersSql = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL,
                role TEXT NOT NULL,
                status TEXT NOT NULL
            )
            """;

        String typesSql = """
            CREATE TABLE IF NOT EXISTS assistance_types (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                type_name TEXT UNIQUE NOT NULL,
                description TEXT,
                status TEXT NOT NULL
            )
            """;

        String requestsSql = """
            CREATE TABLE IF NOT EXISTS assistance_requests (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                applicant_name TEXT NOT NULL,
                applicant_address TEXT NOT NULL,
                contact_number TEXT,
                assistance_type TEXT NOT NULL,
                request_description TEXT,
                date_submitted TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                verification_status TEXT NOT NULL DEFAULT 'PENDING',
                verification_remarks TEXT,
                approval_status TEXT NOT NULL DEFAULT 'PENDING',
                approval_remarks TEXT,
                received_by INTEGER,
                reviewed_by INTEGER,
                approved_by INTEGER,
                FOREIGN KEY (received_by) REFERENCES users(id),
                FOREIGN KEY (reviewed_by) REFERENCES users(id),
                FOREIGN KEY (approved_by) REFERENCES users(id)
            )
            """;

        String logsSql = """
            CREATE TABLE IF NOT EXISTS activity_logs (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER,
                username TEXT,
                action TEXT NOT NULL,
                record_id INTEGER,
                activity_details TEXT,
                timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES users(id)
            )
            """;

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute(usersSql);
            statement.execute(typesSql);
            statement.execute(requestsSql);
            statement.execute(logsSql);

            seedAssistanceTypes(statement);
            createDefaultApprovingOfficer(statement);

            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Database initialization error: " + e.getMessage());
        }
    }

    private static void seedAssistanceTypes(Statement statement) throws SQLException {
        statement.executeUpdate("""
            INSERT OR IGNORE INTO assistance_types(type_name, description, status)
            VALUES
            ('Medical Assistance', 'Assistance for medical-related needs.', 'ACTIVE'),
            ('Emergency Assistance', 'Assistance for urgent emergency situations.', 'ACTIVE'),
            ('Financial Assistance', 'Community financial support request.', 'ACTIVE'),
            ('Other Community Support', 'Other forms of community assistance.', 'ACTIVE')
        """);
    }

    private static void createDefaultApprovingOfficer(Statement statement) throws SQLException {
        try (ResultSet rs = statement.executeQuery(
                "SELECT COUNT(*) FROM users WHERE role = 'APPROVING_OFFICER'")) {

            if (rs.next() && rs.getInt(1) == 0) {
                statement.executeUpdate("""
                    INSERT INTO users(username, password, role, status)
                    VALUES('admin', 'admin123', 'APPROVING_OFFICER', 'ACTIVE')
                """);
                System.out.println("Default approving officer created: admin / admin123");
            }
        }
    }
}
