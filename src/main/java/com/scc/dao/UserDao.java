package com.scc.dao;

import com.scc.Model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    // =====================================================
    // LOGIN
    // =====================================================
    public User loginUser(
            String username,
            String password) {

        String sql = """
                SELECT
                    user_id,
                    username,
                    password,
                    role,
                    status
                FROM users
                WHERE username = ?
                  AND password = ?
                  AND status = 'APPROVED'
                """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, username);
            statement.setString(2, password);

            try (ResultSet result =
                         statement.executeQuery()) {

                if (result.next()) {
                    return mapUser(result);
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Login Error: " + e.getMessage()
            );
        }

        return null;
    }

    // =====================================================
    // REGISTER USER
    // =====================================================
    public boolean registerUser(
            String username,
            String password,
            String role) {

        String sql = """
                INSERT INTO users
                (username, password, role, status)
                VALUES (?, ?, ?, 'PENDING')
                """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, role);

            statement.executeUpdate();

            return true;

        } catch (SQLException e) {

            System.out.println(
                    "Registration Error: "
                            + e.getMessage()
            );

            return false;
        }
    }

    // =====================================================
    // GET ALL USERS
    // =====================================================
    public List<User> getAllUsers() {

        List<User> users = new ArrayList<>();

        String sql = """
                SELECT
                    user_id,
                    username,
                    password,
                    role,
                    status
                FROM users
                ORDER BY user_id
                """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet result =
                     statement.executeQuery()) {

            while (result.next()) {
                users.add(mapUser(result));
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error retrieving users: "
                            + e.getMessage()
            );
        }

        return users;
    }

    // =====================================================
    // GET PENDING USERS
    // =====================================================
    public List<User> getPendingUsers() {

        List<User> users = new ArrayList<>();

        String sql = """
                SELECT
                    user_id,
                    username,
                    password,
                    role,
                    status
                FROM users
                WHERE status = 'PENDING'
                ORDER BY user_id
                """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet result =
                     statement.executeQuery()) {

            while (result.next()) {
                users.add(mapUser(result));
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error retrieving pending users: "
                            + e.getMessage()
            );
        }

        return users;
    }

    // =====================================================
    // APPROVE USER
    // =====================================================
    public boolean approveUser(int userId) {

        String sql = """
                UPDATE users
                SET status = 'APPROVED'
                WHERE user_id = ?
                  AND status = 'PENDING'
                """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            int rowsUpdated =
                    statement.executeUpdate();

            return rowsUpdated > 0;

        } catch (SQLException e) {

            System.out.println(
                    "Error approving user: "
                            + e.getMessage()
            );

            return false;
        }
    }

    // =====================================================
    // FIND USER BY ID
    // =====================================================
    public User getUserById(int userId) {

        String sql = """
                SELECT
                    user_id,
                    username,
                    password,
                    role,
                    status
                FROM users
                WHERE user_id = ?
                """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            try (ResultSet result =
                         statement.executeQuery()) {

                if (result.next()) {
                    return mapUser(result);
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error finding user: "
                            + e.getMessage()
            );
        }

        return null;
    }

    // =====================================================
    // CHECK USERNAME
    // =====================================================
    public boolean usernameExists(String username) {

        String sql = """
                SELECT user_id
                FROM users
                WHERE username = ?
                """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet result =
                         statement.executeQuery()) {

                return result.next();
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error checking username: "
                            + e.getMessage()
            );

            return false;
        }
    }

    // =====================================================
    // MAP RESULT TO USER
    // =====================================================
    private User mapUser(ResultSet result)
            throws SQLException {

        return new User(
                result.getInt("user_id"),
                result.getString("username"),
                result.getString("password"),
                result.getString("role"),
                result.getString("status")
        );
    }
}