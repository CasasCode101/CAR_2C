package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.Model.User;

import java.util.List;
import java.util.Scanner;

public class AdminMenu {

    private final UserDao userDao;
    private final Scanner scanner;

    public AdminMenu(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void start(User admin) {

        while (true) {

            System.out.println("\n========== ADMIN MENU ==========");
            System.out.println("1. View All System Users");
            System.out.println("2. View Pending Registrations");
            System.out.println("3. Approve User Account");
            System.out.println("4. Update User Role and Status");
            System.out.println("5. Logout");

            int choice = readIntInput("Enter choice: ");

            switch (choice) {

                case 1:
                    viewAllUsers();
                    break;

                case 2:
                    viewPendingUsers();
                    break;

                case 3:
                    approveUser();
                    break;

                case 4:
                    updateUser(admin);
                    break;

                case 5:
                    System.out.println("Admin logged out.");
                    return;

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    // ================= VIEW ALL USERS =================

    private void viewAllUsers() {

        List<User> users = userDao.getAllUsers();

        displayUsers(users);
    }

    // ================= VIEW PENDING USERS =================

    private void viewPendingUsers() {

        List<User> users = userDao.getPendingUsers();

        displayUsers(users);
    }

    // ================= DISPLAY USERS =================

    private void displayUsers(List<User> users) {

        System.out.println("\n========== SYSTEM USERS ==========");

        System.out.printf(
                "%-5s %-15s %-22s %-12s%n",
                "ID", "USERNAME", "ROLE", "STATUS"
        );

        System.out.println(
                "------------------------------------------------------------"
        );

        for (User user : users) {

            System.out.printf(
                    "%-5d %-15s %-22s %-12s%n",
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

    // ================= APPROVE USER =================

    private void approveUser() {

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
            System.out.println("Operation cancelled.");
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
            System.out.println("User approved successfully!");
        } else {
            System.out.println("Failed to approve user.");
        }
    }

    // ================= UPDATE USER =================

    private void updateUser(User currentAdmin) {

        viewAllUsers();

        int targetUserId = readIntInput(
                "\nEnter User ID to update (0 to cancel): "
        );

        if (targetUserId == 0) {
            System.out.println("Update cancelled.");
            return;
        }

        // SELF-EDIT RESTRICTION
        if (targetUserId == currentAdmin.id()) {

            System.out.println(
                    "WARNING: Modifying the currently active admin account is restricted."
            );

            return;
        }

        // Check target user
        User targetUser = userDao.getUserById(targetUserId);

        if (targetUser == null) {
            System.out.println("User not found.");
            return;
        }

        System.out.println("\nSelected User:");
        System.out.println(targetUser);

        // Read new role
        String newRole = readValidRole();

        if (newRole == null) {
            System.out.println("Update cancelled.");
            return;
        }

        // Read new status
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
            System.out.println("User role and status updated successfully!");
        } else {
            System.out.println("Failed to update user.");
        }
    }

    // ================= INTEGER VALIDATION =================

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

    // ================= ROLE VALIDATION =================

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

    // ================= STATUS VALIDATION =================

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