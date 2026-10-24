package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.Model.User;

import java.util.Scanner;


public class Authentication {

    private final UserDao userDao;
    private final Scanner scanner;

    public Authentication(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void start() {
        boolean running = true;

        while (running) {
            System.out.println("\n==============================================");
            System.out.println(" COMMUNITY ASSISTANCE REQUEST AND APPROVAL");
            System.out.println("==============================================");
            System.out.println("[1] Login");
            System.out.println("[2] Register Account");
            System.out.println("[3] Exit");
            System.out.print("Select choice: ");

            switch (scanner.nextLine()) {
                case "1" -> handleLogin();
                case "2" -> handleRegister();
                case "3" -> {
                    running = false;
                    System.out.println("Exiting system. Goodbye!");
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void handleLogin() {
        System.out.println("\n========== LOGIN ==========");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        User currentUser = userDao.loginUser(username, password);
        if (currentUser == null) {
            return;
        }

        routeByRole(currentUser);
    }

    private void handleRegister() {
        System.out.println("\n========== REGISTER ==========");
        System.out.print("Enter new Username: ");
        String username = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        System.out.println("\nSelect Role:");
        System.out.println("[1] Receiving Clerk");
        System.out.println("[2] Verifying Officer");
        System.out.println("[3] Approving Officer");
        System.out.print("Choice: ");

        String role = switch (scanner.nextLine()) {
            case "1" -> "RECEIVING_CLERK";
            case "2" -> "VERIFYING_OFFICER";
            case "3" -> "APPROVING_OFFICER";
            default -> null;
        };

        if (role == null) {
            System.out.println("Invalid role selection!");
            return;
        }

        userDao.registerUser(username, password, role);
    }

    private void routeByRole(User currentUser) {
        switch (currentUser.role().toUpperCase()) {
            case "RECEIVING_CLERK" ->
                    ReceivingClerkDashBoard.display(currentUser, scanner);
            case "VERIFYING_OFFICER" ->
                    VerifyingOfficerDashBoard.display(currentUser, scanner);
            case "APPROVING_OFFICER" ->
                    ApprovingOfficerDashBoard.display(currentUser, scanner);
            default -> System.out.println("Unknown role found: " + currentUser.role());
        }
    }
}
