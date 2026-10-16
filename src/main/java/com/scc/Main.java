package com.scc;

import com.scc.dao.UserDao;
import java.util.Scanner;

public class Main {
    private static final Scanner sc;
    private static final UserDao userDao;

    public Main() {
    }

    public static void main(String[] args) {
        System.out.println("\n==============================================");
        System.out.println(" COMMUNITY ASSISTANCE REQUEST SYSTEM");
        System.out.println("==============================================");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");
        switch (sc.nextLine()) {
            case "1" -> login();
            case "2" -> register();
            case "3" -> System.out.println("Thank you for using the system.");
            default -> System.out.println("Invalid option!");
        }

        sc.close();
    }

    private static void login() {
        System.out.println("\n========== LOGIN ==========");
        System.out.print("Enter username: ");
        String username = sc.nextLine();
        System.out.print("Enter password: ");
        String password = sc.nextLine();
        System.out.println("\nLogin information received.");
        System.out.println("Username: " + username);
    }

    private static void register() {
        System.out.println("\n========== REGISTER ==========");
        System.out.print("Enter username: ");
        String username = sc.nextLine();
        System.out.print("Enter password: ");
        String password = sc.nextLine();
        System.out.println("\nSelect Role:");
        System.out.println("[1] RECEIVING CLERK");
        System.out.println("[2] VERIFYING OFFICER");
        System.out.println("[3] APPROVING OFFICER");
        System.out.print("Choose role: ");

        int roleChoice;
        try {
            roleChoice = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException var4) {
            System.out.println("Invalid role selection!");
            return;
        }

        String role;
        switch (roleChoice) {
            case 1:
                role = "RECEIVING_CLERK";
                break;
            case 2:
                role = "VERIFYING_OFFICER";
                break;
            case 3:
                role = "APPROVING_OFFICER";
                break;
            default:
                System.out.println("INVALID role selection!");
                return;
        }

        userDao.registerUser(username, password, role);
        System.out.println("\nRegistration successful!");
        System.out.println("Username: " + username);
        System.out.println("Role: " + role);
    }

    static {
        sc = new Scanner(System.in);
        userDao = new UserDao();
    }
}
