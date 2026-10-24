package com.scc.ui;

import com.scc.dao.AssistanceRequestDao;
import com.scc.Model.AssistanceRequest;
import com.scc.Model.User;

import java.util.List;
import java.util.Scanner;

public class ApprovingOfficerDashBoard {

    private static final AssistanceRequestDao requestDao =
            new AssistanceRequestDao();

    private ApprovingOfficerDashBoard() {
    }

    public static void display(User currentUser, Scanner scanner) {

        boolean running = true;

        while (running) {

            System.out.println();
            System.out.println("==============================================");
            System.out.println("       APPROVING OFFICER DASHBOARD");
            System.out.println("==============================================");
            System.out.println("Logged in as: " + currentUser.username());
            System.out.println();
            System.out.println("1. View Verified Requests");
            System.out.println("2. Approve or Reject Request");
            System.out.println("3. View All Requests");
            System.out.println("4. Show Summary Report");
            System.out.println("5. Logout");
            System.out.println("==============================================");

            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {

                case "1" -> viewVerifiedRequests();

                case "2" -> approveOrReject(currentUser, scanner);

                case "3" -> viewAllRequests();

                case "4" -> requestDao.showSummaryReport();

                case "5" -> {
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
    // VIEW VERIFIED REQUESTS
    // =====================================================
    private static void viewVerifiedRequests() {

        System.out.println();
        System.out.println("==============================================");
        System.out.println("            VERIFIED REQUESTS");
        System.out.println("==============================================");

        List<AssistanceRequest> requests =
                requestDao.getVerifiedRequests();

        if (requests.isEmpty()) {
            System.out.println(
                    "There are no verified requests waiting for approval."
            );
            return;
        }

        displayRequests(requests);
    }

    // =====================================================
    // APPROVE OR REJECT
    // =====================================================
    private static void approveOrReject(
            User currentUser,
            Scanner scanner) {

        System.out.println();
        System.out.println("==============================================");
        System.out.println("         APPROVE / REJECT REQUEST");
        System.out.println("==============================================");

        List<AssistanceRequest> requests =
                requestDao.getVerifiedRequests();

        if (requests.isEmpty()) {
            System.out.println(
                    "There are no verified requests waiting for approval."
            );
            return;
        }

        displayRequests(requests);

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
            System.out.println("Action cancelled.");
            return;
        }

        AssistanceRequest selected = null;

        for (AssistanceRequest request : requests) {

            if (request.requestId() == requestId) {
                selected = request;
                break;
            }
        }

        if (selected == null) {
            System.out.println(
                    "Request not found in the verified requests."
            );
            return;
        }

        System.out.println();
        System.out.println("Request Details");
        System.out.println("----------------------------------------------");
        System.out.println(
                "Request ID        : " + selected.requestId()
        );
        System.out.println(
                "Applicant Name    : " + selected.applicantName()
        );
        System.out.println(
                "Applicant Address : " + selected.applicantAddress()
        );
        System.out.println(
                "Contact Number    : " + selected.contactNumber()
        );
        System.out.println(
                "Assistance Type ID: "
                        + selected.assistanceTypeId()
        );
        System.out.println(
                "Description       : "
                        + selected.requestDescription()
        );
        System.out.println(
                "Verification      : "
                        + selected.verificationStatus()
        );
        System.out.println(
                "Verifier Remarks  : "
                        + selected.verificationRemarks()
        );
        System.out.println(
                "Date Submitted    : "
                        + selected.dateSubmitted()
        );
        System.out.println("----------------------------------------------");

        System.out.println();
        System.out.println("Decision");
        System.out.println("1. APPROVE");
        System.out.println("2. REJECT");
        System.out.println("0. Cancel");

        System.out.print("Choose decision: ");
        String decision = scanner.nextLine().trim();

        String approvalStatus;

        switch (decision) {

            case "1" -> approvalStatus = "APPROVED";

            case "2" -> approvalStatus = "REJECTED";

            case "0" -> {
                System.out.println("Action cancelled.");
                return;
            }

            default -> {
                System.out.println("Invalid decision.");
                return;
            }
        }

        System.out.print("Approval Remarks: ");
        String remarks = scanner.nextLine().trim();

        if (remarks.isEmpty()) {
            System.out.println(
                    "Approval remarks cannot be empty."
            );
            return;
        }

        System.out.println();
        System.out.println(
                "Decision: " + approvalStatus
        );
        System.out.println(
                "Remarks: " + remarks
        );

        System.out.print("Confirm decision? (Y/N): ");

        String confirmation =
                scanner.nextLine().trim();

        if (!confirmation.equalsIgnoreCase("Y")) {
            System.out.println("Action cancelled.");
            return;
        }

        boolean success = requestDao.approveOrReject(
                requestId,
                currentUser.id(),
                approvalStatus,
                remarks
        );

        if (success) {
            System.out.println();
            System.out.println(
                    "Request successfully marked as "
                            + approvalStatus + "."
            );
        } else {
            System.out.println();
            System.out.println(
                    "Failed to update approval."
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