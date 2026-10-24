package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.Model.User;

import java.util.Scanner;

public class Authentication {

    private final UserDao userDao;
    private final Scanner scanner;

    public Authentication(
            UserDao userDao,
            Scanner scanner) {

        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void start() {

        boolean running = true;

        while (running) {

            System.out.println();
            System.out.println(
                    "=============================================="
            );
            System.out.println(
                    " COMMUNITY ASSISTANCE REQUEST AND APPROVAL SYSTEM"
            );
            System.out.println(
                    "=============================================="
            );
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.println(
                    "=============================================="
            );

            System.out.print("Choose an option: ");

            String choice =
                    scanner.nextLine().trim();

            switch (choice) {

                case "1" -> handleLogin();

                case "2" -> handleRegister();

                case "3" -> {
                    System.out.println(
                            "Thank you for using the system."
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
    // LOGIN
    // =====================================================
    private void handleLogin() {

        System.out.println();
        System.out.println(
                "============== LOGIN =============="
        );

        System.out.print("Username: ");
        String username =
                scanner.nextLine().trim();

        System.out.print("Password: ");
        String password =
                scanner.nextLine().trim();

        User currentUser =
                userDao.loginUser(
                        username,
                        password
                );

        if (currentUser == null) {

            System.out.println();
            System.out.println(
                    "Invalid username/password "
                            + "or account is not approved."
            );

            return;
        }

        System.out.println();
        System.out.println(
                "Login successful."
        );

        System.out.println(
                "Welcome, "
                        + currentUser.username()
        );

        routeByRole(currentUser);
    }

    // =====================================================
    // REGISTRATION
    // =====================================================
    private void handleRegister() {

        System.out.println();
        System.out.println(
                "=========== REGISTRATION ==========="
        );

        System.out.print("Username: ");
        String username =
                scanner.nextLine().trim();

        if (username.isEmpty()) {

            System.out.println(
                    "Username cannot be empty."
            );

            return;
        }

        if (userDao.usernameExists(username)) {

            System.out.println(
                    "Username already exists."
            );

            return;
        }

        System.out.print("Password: ");
        String password =
                scanner.nextLine().trim();

        if (password.isEmpty()) {

            System.out.println(
                    "Password cannot be empty."
            );

            return;
        }

        System.out.println();
        System.out.println("Select Role:");
        System.out.println(
                "1. Receiving Clerk"
        );
        System.out.println(
                "2. Verifying Officer"
        );
        System.out.println(
                "3. Approving Officer"
        );
        System.out.println(
                "0. Cancel"
        );

        System.out.print("Choose role: ");

        String choice =
                scanner.nextLine().trim();

        String role;

        switch (choice) {

            case "1" ->
                    role = "RECEIVING_CLERK";

            case "2" ->
                    role = "VERIFYING_OFFICER";

            case "3" ->
                    role = "APPROVING_OFFICER";

            case "0" -> {
                System.out.println(
                        "Registration cancelled."
                );
                return;
            }

            default -> {
                System.out.println(
                        "Invalid role."
                );
                return;
            }
        }

        boolean registered =
                userDao.registerUser(
                        username,
                        password,
                        role
                );

        if (registered) {

            System.out.println();
            System.out.println(
                    "Registration successful."
            );

            System.out.println(
                    "Your account is now PENDING."
            );

            System.out.println(
                    "An administrator must approve "
                            + "your account before you can login."
            );

        } else {

            System.out.println(
                    "Registration failed."
            );
        }
    }

    // =====================================================
    // ROLE ROUTING
    // =====================================================
    private void routeByRole(User currentUser) {

        switch (currentUser.role()) {

            case "ADMIN" -> {

                AdminMenu adminMenu =
                        new AdminMenu(
                                userDao,
                                scanner
                        );

                adminMenu.start(currentUser);
            }

            case "RECEIVING_CLERK" ->
                    ReceivingClerkDashBoard.display(
                            currentUser,
                            scanner
                    );

            case "VERIFYING_OFFICER" ->
                    VerifyingOfficerDashBoard.display(
                            currentUser,
                            scanner
                    );

            case "APPROVING_OFFICER" ->
                    ApprovingOfficerDashBoard.display(
                            currentUser,
                            scanner
                    );

            default ->
                    System.out.println(
                            "Unknown user role."
                    );
        }
    }
}
