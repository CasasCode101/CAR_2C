package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.Model.User;

import java.util.List;
import java.util.Scanner;

public class AdminMenu {

    private final UserDao userDao;
    private final Scanner scanner;

    public AdminMenu(
            UserDao userDao,
            Scanner scanner) {

        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void start(User admin) {

        boolean running = true;

        while (running) {

            System.out.println();
            System.out.println("==============================================");
            System.out.println("                 ADMIN MENU");
            System.out.println("==============================================");
            System.out.println(
                    "Logged in as: " + admin.username()
            );
            System.out.println();
            System.out.println("1. View All System Users");
            System.out.println("2. View Pending Registrations");
            System.out.println("3. Approve User Account");
            System.out.println("4. Logout");
            System.out.println("==============================================");

            System.out.print("Choose an option: ");

            String choice =
                    scanner.nextLine().trim();

            switch (choice) {

                case "1" -> viewAllUsers();

                case "2" -> viewPendingUsers();

                case "3" -> approveUser();

                case "4" -> {
                    System.out.println(
                            "Logging out..."
                    );
                    running = false;
                }

                default ->
                        System.out.println(
                                "Invalid option."
                        );
            }
        }
    }

    // =====================================================
    // VIEW ALL USERS
    // =====================================================
    private void viewAllUsers() {

        System.out.println();
        System.out.println(
                "========== ALL SYSTEM USERS =========="
        );

        List<User> users =
                userDao.getAllUsers();

        displayUsers(users);
    }

    // =====================================================
    // VIEW PENDING USERS
    // =====================================================
    private void viewPendingUsers() {

        System.out.println();
        System.out.println(
                "======= PENDING REGISTRATIONS ======="
        );

        List<User> users =
                userDao.getPendingUsers();

        displayUsers(users);
    }

    // =====================================================
    // APPROVE USER
    // =====================================================
    private void approveUser() {

        System.out.println();
        System.out.println(
                "========= APPROVE USER ACCOUNT ========="
        );

        List<User> pendingUsers =
                userDao.getPendingUsers();

        if (pendingUsers.isEmpty()) {

            System.out.println(
                    "There are no pending registrations."
            );

            return;
        }

        displayUsers(pendingUsers);

        System.out.println();
        System.out.print(
                "Enter Target User ID (0 to cancel): "
        );

        String input =
                scanner.nextLine().trim();

        int userId;

        try {
            userId = Integer.parseInt(input);

        } catch (NumberFormatException e) {

            System.out.println(
                    "Please enter a valid numeric ID."
            );

            return;
        }

        // ==========================================
        // CANCEL
        // ==========================================
        if (userId == 0) {

            System.out.println(
                    "Approval cancelled."
            );

            return;
        }

        // ==========================================
        // VERIFY ID EXISTS IN PENDING LIST
        // ==========================================
        User targetUser = null;

        for (User user : pendingUsers) {

            if (user.id() == userId) {
                targetUser = user;
                break;
            }
        }

        if (targetUser == null) {

            System.out.println(
                    "The selected User ID is not in "
                            + "the pending registration list."
            );

            return;
        }

        System.out.println();
        System.out.println(
                "Selected User"
        );
        System.out.println(
                "ID       : " + targetUser.id()
        );
        System.out.println(
                "Username : " + targetUser.username()
        );
        System.out.println(
                "Role     : " + targetUser.role()
        );
        System.out.println(
                "Status   : " + targetUser.status()
        );

        System.out.println();

        System.out.print(
                "Approve this account? (Y/N): "
        );

        String confirmation =
                scanner.nextLine().trim();

        if (!confirmation.equalsIgnoreCase("Y")) {

            System.out.println(
                    "Approval cancelled."
            );

            return;
        }

        boolean approved =
                userDao.approveUser(userId);

        if (approved) {

            System.out.println();
            System.out.println(
                    "User account approved successfully."
            );

        } else {

            System.out.println();
            System.out.println(
                    "Unable to approve user account."
            );
        }
    }

    // =====================================================
    // FORMATTED USER TABLE
    // =====================================================
    private void displayUsers(
            List<User> users) {

        if (users.isEmpty()) {

            System.out.println(
                    "No users found."
            );

            return;
        }

        System.out.println();
        System.out.println(
                "+-----+---------------+------------+----------+"
        );

        System.out.printf(
                "| %-3s | %-13s | %-10s | %-8s |%n",
                "ID",
                "USERNAME",
                "ROLE",
                "STATUS"
        );

        System.out.println(
                "+-----+---------------+------------+----------+"
        );

        for (User user : users) {

            System.out.printf(
                    "| %-3d | %-13s | %-10s | %-8s |%n",
                    user.id(),
                    shorten(user.username(), 13),
                    shorten(user.role(), 10),
                    shorten(user.status(), 8)
            );
        }

        System.out.println(
                "+-----+---------------+------------+----------+"
        );
    }

    // =====================================================
    // SHORTEN LONG TEXT
    // =====================================================
    private String shorten(
            String value,
            int maxLength) {

        if (value == null) {
            return "";
        }

        if (value.length() <= maxLength) {
            return value;
        }

        return value.substring(
                0,
                maxLength - 3
        ) + "...";
    }
}