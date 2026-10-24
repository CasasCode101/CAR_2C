package com.scc.ui;

import com.scc.dao.AssistanceRequestDao;
import com.scc.Model.AssistanceRequest;
import com.scc.Model.User;

import java.util.List;
import java.util.Scanner;

public class VerifyingOfficerDashBoard {

    private static final AssistanceRequestDao requestDao =
            new AssistanceRequestDao();

    private VerifyingOfficerDashBoard() {
    }

    public static void display(User currentUser, Scanner scanner) {

        boolean running = true;

        while (running) {

            System.out.println();
            System.out.println("==============================================");
            System.out.println("       VERIFYING OFFICER DASHBOARD");
            System.out.println("==============================================");
            System.out.println("Logged in as: " + currentUser.username());
            System.out.println();
            System.out.println("1. View Pending Requests");
            System.out.println("2. Verify Request");
            System.out.println("3. View All Requests");
            System.out.println("4. Logout");
            System.out.println("==============================================");

            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {

                case "1" -> viewPendingRequests();

                case "2" -> verifyRequest(currentUser, scanner);

                case "3" -> viewAllRequests();

                case "4" -> {
                    System.out.println("Logging out...");
                    running = false;
                }

                default ->
                        System.out.println(
                                "Invalid option. Please try again."
                        );
            }
        }
    }

    // =====================================================
    // VIEW PENDING REQUESTS
    // =====================================================
    private static void viewPendingRequests() {

        System.out.println();
        System.out.println("==============================================");
        System.out.println("         PENDING VERIFICATION");
        System.out.println("==============================================");

        List<AssistanceRequest> requests =
                requestDao.getPendingVerification();

        if (requests.isEmpty()) {
            System.out.println(
                    "There are no requests waiting for verification."
            );
            return;
        }

        displayRequests(requests);
    }

    // =====================================================
    // VERIFY REQUEST
    // =====================================================
    private static void verifyRequest(
            User currentUser,
            Scanner scanner) {

        System.out.println();
        System.out.println("==============================================");
        System.out.println("            VERIFY REQUEST");
        System.out.println("==============================================");

        List<AssistanceRequest> pending =
                requestDao.getPendingVerification();

        if (pending.isEmpty()) {
            System.out.println(
                    "There are no requests waiting for verification."
            );
            return;
        }

        displayRequests(pending);

        System.out.println();
        System.out.print("Enter Request ID (0 to cancel): ");

        String input = scanner.nextLine().trim();

        int requestId;

        try {
            requestId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid Request ID.");
            return;
        }

        if (requestId == 0) {
            System.out.println("Verification cancelled.");
            return;
        }

        AssistanceRequest selected = null;

        for (AssistanceRequest request : pending) {

            if (request.requestId() == requestId) {
                selected = request;
                break;
            }
        }

        if (selected == null) {
            System.out.println(
                    "Request not found in pending verification."
            );
            return;
        }

        System.out.println();
        System.out.println("Request Details");
        System.out.println("----------------------------------------------");
        System.out.println(
                "Request ID       : " + selected.requestId()
        );
        System.out.println(
                "Applicant Name   : " + selected.applicantName()
        );
        System.out.println(
                "Address          : " + selected.applicantAddress()
        );
        System.out.println(
                "Contact Number   : " + selected.contactNumber()
        );
        System.out.println(
                "Assistance Type ID: "
                        + selected.assistanceTypeId()
        );
        System.out.println(
                "Description      : "
                        + selected.requestDescription()
        );
        System.out.println(
                "Date Submitted   : "
                        + selected.dateSubmitted()
        );
        System.out.println("----------------------------------------------");

        System.out.println();
        System.out.println("Verification Result");
        System.out.println("1. VERIFIED");
        System.out.println("2. REJECTED");
        System.out.println("0. Cancel");

        System.out.print("Choose result: ");
        String result = scanner.nextLine().trim();

        String verificationStatus;

        switch (result) {

            case "1" -> verificationStatus = "VERIFIED";

            case "2" -> verificationStatus = "REJECTED";

            case "0" -> {
                System.out.println("Verification cancelled.");
                return;
            }

            default -> {
                System.out.println("Invalid verification result.");
                return;
            }
        }

        System.out.print("Verification Remarks: ");
        String remarks = scanner.nextLine().trim();

        if (remarks.isEmpty()) {
            System.out.println(
                    "Verification remarks cannot be empty."
            );
            return;
        }

        System.out.println();
        System.out.println(
                "Status: " + verificationStatus
        );
        System.out.println(
                "Remarks: " + remarks
        );

        System.out.print("Confirm verification? (Y/N): ");

        String confirmation =
                scanner.nextLine().trim();

        if (!confirmation.equalsIgnoreCase("Y")) {
            System.out.println("Verification cancelled.");
            return;
        }

        boolean success = requestDao.verifyRequest(
                requestId,
                currentUser.id(),
                verificationStatus,
                remarks
        );

        if (success) {
            System.out.println();
            System.out.println(
                    "Request verified successfully."
            );
        } else {
            System.out.println();
            System.out.println(
                    "Failed to update verification."
            );
        }
    }

    // =====================================================
    // VIEW ALL REQUESTS
    // =====================================================
    private static void viewAllRequests() {

        System.out.println();
        System.out.println("==============================================");
        System.out.println("          ALL ASSISTANCE REQUESTS");
        System.out.println("==============================================");

        List<AssistanceRequest> requests =
                requestDao.getAllRequests();

        if (requests.isEmpty()) {
            System.out.println(
                    "No assistance requests found."
            );
            return;
        }

        displayRequests(requests);
    }

    // =====================================================
    // DISPLAY REQUESTS
    // =====================================================
    private static void displayRequests(
            List<AssistanceRequest> requests) {

        System.out.printf(
                "%-5s %-22s %-12s %-15s %-12s%n",
                "ID",
                "APPLICANT",
                "TYPE ID",
                "VERIFICATION",
                "APPROVAL"
        );

        System.out.println(
                "----------------------------------------------------------------"
        );

        for (AssistanceRequest request : requests) {

            System.out.printf(
                    "%-5d %-22s %-12d %-15s %-12s%n",
                    request.requestId(),
                    shorten(
                            request.applicantName(),
                            22
                    ),
                    request.assistanceTypeId(),
                    request.verificationStatus(),
                    request.approvalStatus()
            );
        }
    }

    // =====================================================
    // SHORTEN TEXT
    // =====================================================
    private static String shorten(
            String text,
            int maxLength) {

        if (text == null) {
            return "";
        }

        if (text.length() <= maxLength) {
            return text;
        }

        return text.substring(
                0,
                maxLength - 3
        ) + "...";
    }
}