package com.scc.ui;

import com.scc.dao.ActivityLogDao;
import com.scc.dao.UserDao;
import com.scc.Model.ActivityLog;
import com.scc.Model.User;

import java.util.List;
import java.util.Scanner;

public class AdminMenu {

    private final UserDao userDao;
    private final ActivityLogDao activityLogDao;
    private final Scanner scanner;

    public AdminMenu(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.activityLogDao = new ActivityLogDao();
        this.scanner = scanner;
    }

    // =========================================
    // ADMIN MENU
    // =========================================

    public void start(User admin) {

        while (true) {

            System.out.println("\n========== ADMIN MENU ==========");
            System.out.println("1. View All System Users");
            System.out.println("2. View Pending Registrations");
            System.out.println("3. Approve User Account");
            System.out.println("4. Update User Role and Status");
            System.out.println("5. View System Audit Logs");
            System.out.println("6. Filter Audit Logs by User ID");
            System.out.println("7. Clear Historical Logs");
            System.out.println("8. Logout");

            int choice = readIntInput("Enter choice: ");

            switch (choice) {

                case 1:
                    viewAllUsers();
                    break;

                case 2:
                    viewPendingUsers();
                    break;

                case 3:
                    approveUser(admin);
                    break;

                case 4:
                    updateUser(admin);
                    break;

                case 5:
                    viewAllLogs();
                    break;

                case 6:
                    viewLogsByUserId();
                    break;

                case 7:
                    clearAllLogs();
                    break;

                case 8:
                    System.out.println("Admin logged out.");
                    return;

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    // =========================================
    // VIEW USERS
    // =========================================

    private void viewAllUsers() {
        displayUsers(userDao.getAllUsers());
    }

    private void viewPendingUsers() {
        displayUsers(userDao.getPendingUsers());
    }

    private void displayUsers(List<User> users) {

        System.out.println("\n========== SYSTEM USERS ==========");

        System.out.printf(
                "%-5s | %-15s | %-22s | %-12s%n",
                "ID", "USERNAME", "ROLE", "STATUS"
        );

        System.out.println(
                "------------------------------------------------------------"
        );

        for (User user : users) {

            System.out.printf(
                    "%-5d | %-15s | %-22s | %-12s%n",
                    user.id(),
                    user.username(),
                    user.role(),
                    user.status()
            );
        }

        if (users.isEmpty()) {
            System.out.println("No users found.");
        }
    }

    // =========================================
    // APPROVE USER
    // =========================================

    private void approveUser(User admin) {

        List<User> pendingUsers = userDao.getPendingUsers();

        if (pendingUsers.isEmpty()) {
            System.out.println("No pending users.");
            return;
        }

        displayUsers(pendingUsers);

        int userId = readIntInput(
                "\nEnter User ID to approve (0 to cancel): "
        );

        if (userId == 0) {
            System.out.println("Approval cancelled.");
            return;
        }

        boolean exists = pendingUsers.stream()
                .anyMatch(user -> user.id() == userId);

        if (!exists) {
            System.out.println("Invalid pending User ID.");
            return;
        }

        System.out.print("Confirm approval? (Y/N): ");

        String confirmation = scanner.nextLine().trim();

        if (!confirmation.equalsIgnoreCase("Y")) {
            System.out.println("Approval cancelled.");
            return;
        }

        boolean approved = userDao.approveUser(userId);

        if (approved) {

            activityLogDao.logAction(
                    admin.id(),
                    "USER_APPROVED",
                    userId,
                    "Admin [" + admin.id()
                            + "] approved account for User ID "
                            + userId
            );

            System.out.println("User approved successfully!");

        } else {
            System.out.println("Failed to approve user.");
        }
    }

    // =========================================
    // UPDATE USER ROLE AND STATUS
    // =========================================

    private void updateUser(User currentAdmin) {

        viewAllUsers();

        int targetUserId = readIntInput(
                "\nEnter User ID to update (0 to cancel): "
        );

        if (targetUserId == 0) {
            System.out.println("Update cancelled.");
            return;
        }

        // SECURITY: PREVENT ADMIN SELF-EDIT
        if (targetUserId == currentAdmin.id()) {

            System.out.println(
                    "WARNING: Modifying the currently active admin account is restricted."
            );

            return;
        }

        User targetUser = userDao.getUserById(targetUserId);

        if (targetUser == null) {
            System.out.println("User not found.");
            return;
        }

        System.out.println("\nSelected User:");
        System.out.println(targetUser);

        String newRole = readValidRole();

        if (newRole == null) {
            System.out.println("Update cancelled.");
            return;
        }

        String newStatus = readValidStatus();

        if (newStatus == null) {
            System.out.println("Update cancelled.");
            return;
        }

        System.out.println("\nNew Role: " + newRole);
        System.out.println("New Status: " + newStatus);

        System.out.print("Confirm update? (Y/N): ");

        String confirmation = scanner.nextLine().trim();

        if (!confirmation.equalsIgnoreCase("Y")) {
            System.out.println("Update cancelled.");
            return;
        }

        boolean updated = userDao.updateUserRoleAndStatus(
                targetUserId,
                newRole,
                newStatus
        );

        if (updated) {

            activityLogDao.logAction(
                    currentAdmin.id(),
                    "USER_UPDATED",
                    targetUserId,
                    "Admin [" + currentAdmin.id()
                            + "] changed role of User "
                            + targetUserId
                            + " to " + newRole
                            + " and status to " + newStatus
            );

            System.out.println(
                    "User role and status updated successfully!"
            );

        } else {
            System.out.println("Failed to update user.");
        }
    }

    // =========================================
    // VIEW ALL AUDIT LOGS
    // =========================================

    private void viewAllLogs() {
        displayLogs(activityLogDao.getAllLogs());
    }

    // =========================================
    // FILTER LOGS BY USER ID
    // =========================================

    private void viewLogsByUserId() {

        int userId = readIntInput(
                "Enter User ID to filter (0 to cancel): "
        );

        if (userId == 0) {
            System.out.println("Filter cancelled.");
            return;
        }

        displayLogs(activityLogDao.getLogsByUserId(userId));
    }

    // =========================================
    // DISPLAY LOGS
    // =========================================

    private void displayLogs(List<ActivityLog> logs) {

        System.out.println("\n========== SYSTEM AUDIT LOGS ==========");

        System.out.printf(
                "%-5s | %-8s | %-18s | %-35s | %-20s%n",
                "ID",
                "USER ID",
                "ACTION",
                "DESCRIPTION",
                "TIMESTAMP"
        );

        System.out.println(
                "----------------------------------------------------------------------------------------------"
        );

        for (ActivityLog log : logs) {

            String description = log.description();

            // Keep description inside its table column
            if (description != null && description.length() > 35) {
                description = description.substring(0, 32) + "...";
            }

            System.out.printf(
                    "%-5d | %-8d | %-18s | %-35s | %-20s%n",
                    log.logId(),
                    log.userId(),
                    log.actionType(),
                    description,
                    log.timestamp()
            );
        }

        if (logs.isEmpty()) {
            System.out.println("No activity logs found.");
        }
    }

    // =========================================
    // CLEAR HISTORICAL LOGS
    // =========================================

    private void clearAllLogs() {

        System.out.println(
                "\nWARNING: This will permanently delete all historical logs."
        );

        System.out.print("Continue? (Y/N): ");

        String confirmation = scanner.nextLine().trim();

        if (!confirmation.equalsIgnoreCase("Y")) {
            System.out.println("Clear logs cancelled.");
            return;
        }

        boolean cleared = activityLogDao.clearAllLogs();

        if (cleared) {
            System.out.println("Historical logs cleared successfully.");
        } else {
            System.out.println("Failed to clear logs.");
        }
    }

    // =========================================
    // INTEGER INPUT VALIDATION
    // =========================================

    private int readIntInput(String prompt) {

        while (true) {

            System.out.print(prompt);

            String input = scanner.nextLine().trim();

            try {

                return Integer.parseInt(input);

            } catch (NumberFormatException e) {

                System.out.println(
                        "Invalid input! Please enter a number."
                );
            }
        }
    }

    // =========================================
    // ROLE VALIDATION
    // =========================================

    private String readValidRole() {

        while (true) {

            System.out.println("\n========== AVAILABLE ROLES ==========");
            System.out.println("1. ADMIN");
            System.out.println("2. RECEIVING_CLERK");
            System.out.println("3. VERIFYING_OFFICER");
            System.out.println("4. APPROVING_OFFICER");
            System.out.println("0. Cancel");

            int choice = readIntInput("Select Role: ");

            switch (choice) {

                case 1:
                    return "ADMIN";

                case 2:
                    return "RECEIVING_CLERK";

                case 3:
                    return "VERIFYING_OFFICER";

                case 4:
                    return "APPROVING_OFFICER";

                case 0:
                    return null;

                default:
                    System.out.println("Invalid role selection!");
            }
        }
    }

    // =========================================
    // STATUS VALIDATION
    // =========================================

    private String readValidStatus() {

        while (true) {

            System.out.println("\n========== ACCOUNT STATUS ==========");
            System.out.println("1. APPROVED");
            System.out.println("2. PENDING");
            System.out.println("0. Cancel");

            int choice = readIntInput("Select Status: ");

            switch (choice) {

                case 1:
                    return "APPROVED";

                case 2:
                    return "PENDING";

                case 0:
                    return null;

                default:
                    System.out.println("Invalid status selection!");
            }
        }
    }
}