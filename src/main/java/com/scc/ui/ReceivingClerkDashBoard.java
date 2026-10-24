package com.scc.ui;

import com.scc.dao.AssistanceRequestDao;
import com.scc.Model.AssistanceRequest;
import com.scc.Model.User;

import java.util.List;
import java.util.Scanner;

public class ReceivingClerkDashBoard {

    private static final AssistanceRequestDao requestDao =
            new AssistanceRequestDao();

    private ReceivingClerkDashBoard() {
    }

    public static void display(User currentUser, Scanner scanner) {

        boolean running = true;

        while (running) {

            System.out.println();
            System.out.println("==============================================");
            System.out.println("        RECEIVING CLERK DASHBOARD");
            System.out.println("==============================================");
            System.out.println("Logged in as: " + currentUser.username());
            System.out.println();
            System.out.println("1. Create Assistance Request");
            System.out.println("2. View Assistance Requests");
            System.out.println("3. Update Assistance Request");
            System.out.println("4. Delete Assistance Request");
            System.out.println("5. Logout");
            System.out.println("==============================================");

            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {

                case "1" -> createRequest(currentUser, scanner);

                case "2" -> viewRequests();

                case "3" -> updateRequest(scanner);

                case "4" -> deleteRequest(scanner);

                case "5" -> {
                    System.out.println("Logging out...");
                    running = false;
                }

                default ->
                        System.out.println("Invalid option. Please try again.");
            }
        }
    }

    // =====================================================
    // CREATE REQUEST
    // =====================================================
    private static void createRequest(
            User currentUser,
            Scanner scanner) {

        System.out.println();
        System.out.println("==============================================");
        System.out.println("       CREATE ASSISTANCE REQUEST");
        System.out.println("==============================================");

        System.out.print("Applicant Name: ");
        String applicantName = scanner.nextLine().trim();

        if (applicantName.isEmpty()) {
            System.out.println("Applicant name cannot be empty.");
            return;
        }

        System.out.print("Applicant Address: ");
        String applicantAddress = scanner.nextLine().trim();

        if (applicantAddress.isEmpty()) {
            System.out.println("Applicant address cannot be empty.");
            return;
        }

        System.out.print("Contact Number: ");
        String contactNumber = scanner.nextLine().trim();

        int assistanceTypeId = selectAssistanceType(scanner);

        if (assistanceTypeId == -1) {
            System.out.println("Request creation cancelled.");
            return;
        }

        System.out.print("Request Description: ");
        String requestDescription = scanner.nextLine().trim();

        boolean success = requestDao.createRequest(
                applicantName,
                applicantAddress,
                contactNumber,
                assistanceTypeId,
                requestDescription,
                currentUser.id()
        );

        if (success) {
            System.out.println();
            System.out.println("Request created successfully.");
        } else {
            System.out.println();
            System.out.println("Failed to create request.");
        }
    }

    // =====================================================
    // SELECT ASSISTANCE TYPE
    // =====================================================
    private static int selectAssistanceType(Scanner scanner) {

        List<String[]> types = requestDao.getAssistanceTypes();

        if (types.isEmpty()) {
            System.out.println("No active assistance types available.");
            return -1;
        }

        System.out.println();
        System.out.println("Available Assistance Types");
        System.out.println("----------------------------------------------");

        for (String[] type : types) {
            System.out.printf(
                    "%-5s %-30s%n",
                    type[0],
                    type[1]
            );
        }

        System.out.println("----------------------------------------------");
        System.out.print("Enter Assistance Type ID (0 to cancel): ");

        String input = scanner.nextLine().trim();

        try {

            int typeId = Integer.parseInt(input);

            if (typeId == 0) {
                return -1;
            }

            for (String[] type : types) {
                if (Integer.parseInt(type[0]) == typeId) {
                    return typeId;
                }
            }

            System.out.println("Invalid assistance type ID.");

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }

        return -1;
    }

    // =====================================================
    // VIEW REQUESTS
    // =====================================================
    private static void viewRequests() {

        System.out.println();
        System.out.println("==============================================");
        System.out.println("          ASSISTANCE REQUESTS");
        System.out.println("==============================================");

        List<AssistanceRequest> requests =
                requestDao.getAllRequests();

        if (requests.isEmpty()) {
            System.out.println("No assistance requests found.");
            return;
        }

        System.out.printf(
                "%-5s %-22s %-18s %-12s %-12s %-12s%n",
                "ID",
                "APPLICANT",
                "TYPE ID",
                "VERIFY",
                "APPROVAL",
                "RECEIVED BY"
        );

        System.out.println(
                "-------------------------------------------------------------------------------"
        );

        for (AssistanceRequest request : requests) {

            System.out.printf(
                    "%-5d %-22s %-18d %-12s %-12s %-12s%n",
                    request.requestId(),
                    shorten(request.applicantName(), 22),
                    request.assistanceTypeId(),
                    request.verificationStatus(),
                    request.approvalStatus(),
                    request.receivedBy() == null
                            ? "-"
                            : request.receivedBy()
            );
        }
    }

    // =====================================================
    // UPDATE REQUEST
    // =====================================================
    private static void updateRequest(Scanner scanner) {

        System.out.println();
        System.out.println("==============================================");
        System.out.println("          UPDATE ASSISTANCE REQUEST");
        System.out.println("==============================================");

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
            System.out.println("Update cancelled.");
            return;
        }

        AssistanceRequest existing =
                requestDao.getRequestById(requestId);

        if (existing == null) {
            System.out.println("Request not found.");
            return;
        }

        // Do not allow editing after verification has started.
        if (!"PENDING".equalsIgnoreCase(
                existing.verificationStatus())) {

            System.out.println(
                    "This request can no longer be edited because "
                            + "verification has already started."
            );
            return;
        }

        System.out.println();
        System.out.println(
                "Current Applicant: " + existing.applicantName()
        );

        System.out.print("New Applicant Name: ");
        String applicantName = scanner.nextLine().trim();

        if (applicantName.isEmpty()) {
            applicantName = existing.applicantName();
        }

        System.out.print("New Applicant Address: ");
        String applicantAddress = scanner.nextLine().trim();

        if (applicantAddress.isEmpty()) {
            applicantAddress = existing.applicantAddress();
        }

        System.out.print("New Contact Number: ");
        String contactNumber = scanner.nextLine().trim();

        if (contactNumber.isEmpty()) {
            contactNumber = existing.contactNumber();
        }

        System.out.println();
        System.out.println(
                "Current Assistance Type ID: "
                        + existing.assistanceTypeId()
        );

        System.out.print(
                "Change assistance type? (Y/N): "
        );

        String changeType = scanner.nextLine().trim();

        int assistanceTypeId =
                existing.assistanceTypeId();

        if (changeType.equalsIgnoreCase("Y")) {

            int selectedType =
                    selectAssistanceType(scanner);

            if (selectedType == -1) {
                System.out.println("Update cancelled.");
                return;
            }

            assistanceTypeId = selectedType;
        }

        System.out.print("New Request Description: ");
        String requestDescription = scanner.nextLine().trim();

        if (requestDescription.isEmpty()) {
            requestDescription =
                    existing.requestDescription();
        }

        System.out.println();
        System.out.print("Confirm update? (Y/N): ");

        String confirmation =
                scanner.nextLine().trim();

        if (!confirmation.equalsIgnoreCase("Y")) {
            System.out.println("Update cancelled.");
            return;
        }

        boolean success = requestDao.updateRequest(
                requestId,
                applicantName,
                applicantAddress,
                contactNumber,
                assistanceTypeId,
                requestDescription
        );

        if (success) {
            System.out.println(
                    "Request updated successfully."
            );
        } else {
            System.out.println(
                    "Failed to update request."
            );
        }
    }

    // =====================================================
    // DELETE REQUEST
    // =====================================================
    private static void deleteRequest(Scanner scanner) {

        System.out.println();
        System.out.println("==============================================");
        System.out.println("          DELETE ASSISTANCE REQUEST");
        System.out.println("==============================================");

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
            System.out.println("Delete cancelled.");
            return;
        }

        AssistanceRequest existing =
                requestDao.getRequestById(requestId);

        if (existing == null) {
            System.out.println("Request not found.");
            return;
        }

        // Do not allow deletion after verification.
        if (!"PENDING".equalsIgnoreCase(
                existing.verificationStatus())) {

            System.out.println(
                    "This request cannot be deleted because "
                            + "verification has already started."
            );
            return;
        }

        System.out.println(
                "Applicant: " + existing.applicantName()
        );

        System.out.print("Are you sure? (Y/N): ");

        String confirmation =
                scanner.nextLine().trim();

        if (!confirmation.equalsIgnoreCase("Y")) {
            System.out.println("Delete cancelled.");
            return;
        }

        boolean success =
                requestDao.deleteRequest(requestId);

        if (success) {
            System.out.println(
                    "Request deleted successfully."
            );
        } else {
            System.out.println(
                    "Failed to delete request."
            );
        }
    }

    // =====================================================
    // SHORTEN LONG TEXT FOR TABLE DISPLAY
    // =====================================================
    private static String shorten(String text, int maxLength) {

        if (text == null) {
            return "";
        }

        if (text.length() <= maxLength) {
            return text;
        }

        return text.substring(0, maxLength - 3) + "...";
    }
}