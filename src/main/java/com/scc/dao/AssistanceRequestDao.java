package com.scc.dao;

import com.scc.Model.AssistanceRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AssistanceRequestDao {

    // =====================================================
    // CREATE REQUEST
    // =====================================================
    public boolean createRequest(
            String applicantName,
            String applicantAddress,
            String contactNumber,
            int assistanceTypeId,
            String requestDescription,
            int receivedBy) {

        String sql = """
                INSERT INTO assistance_requests (
                    applicant_name,
                    applicant_address,
                    contact_number,
                    assistance_type_id,
                    request_description,
                    received_by
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, applicantName);
            statement.setString(2, applicantAddress);
            statement.setString(3, contactNumber);
            statement.setInt(4, assistanceTypeId);
            statement.setString(5, requestDescription);
            statement.setInt(6, receivedBy);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Create request failed: " + e.getMessage());
            return false;
        }
    }

    // =====================================================
    // GET ALL REQUESTS
    // =====================================================
    public List<AssistanceRequest> getAllRequests() {

        List<AssistanceRequest> requests = new ArrayList<>();

        String sql = """
                SELECT
                    request_id,
                    applicant_name,
                    applicant_address,
                    contact_number,
                    assistance_type_id,
                    request_description,
                    date_submitted,
                    verification_status,
                    verification_remarks,
                    approval_status,
                    approval_remarks,
                    received_by,
                    reviewed_by,
                    approved_by
                FROM assistance_requests
                ORDER BY request_id DESC
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                requests.add(mapRequest(result));
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving requests: " + e.getMessage());
        }

        return requests;
    }

    // =====================================================
    // GET REQUEST BY ID
    // =====================================================
    public AssistanceRequest getRequestById(int requestId) {

        String sql = """
                SELECT
                    request_id,
                    applicant_name,
                    applicant_address,
                    contact_number,
                    assistance_type_id,
                    request_description,
                    date_submitted,
                    verification_status,
                    verification_remarks,
                    approval_status,
                    approval_remarks,
                    received_by,
                    reviewed_by,
                    approved_by
                FROM assistance_requests
                WHERE request_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, requestId);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    return mapRequest(result);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving request: " + e.getMessage());
        }

        return null;
    }

    // =====================================================
    // GET PENDING VERIFICATION
    // =====================================================
    public List<AssistanceRequest> getPendingVerification() {

        List<AssistanceRequest> requests = new ArrayList<>();

        String sql = """
                SELECT
                    request_id,
                    applicant_name,
                    applicant_address,
                    contact_number,
                    assistance_type_id,
                    request_description,
                    date_submitted,
                    verification_status,
                    verification_remarks,
                    approval_status,
                    approval_remarks,
                    received_by,
                    reviewed_by,
                    approved_by
                FROM assistance_requests
                WHERE verification_status = 'PENDING'
                ORDER BY request_id ASC
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                requests.add(mapRequest(result));
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error retrieving pending verification requests: "
                            + e.getMessage()
            );
        }

        return requests;
    }

    // =====================================================
    // VERIFY REQUEST
    // =====================================================
    public boolean verifyRequest(
            int requestId,
            int reviewedBy,
            String verificationStatus,
            String verificationRemarks) {

        String sql = """
                UPDATE assistance_requests
                SET
                    verification_status = ?,
                    verification_remarks = ?,
                    reviewed_by = ?
                WHERE request_id = ?
                  AND verification_status = 'PENDING'
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, verificationStatus);
            statement.setString(2, verificationRemarks);
            statement.setInt(3, reviewedBy);
            statement.setInt(4, requestId);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println(
                    "Verification failed: " + e.getMessage()
            );
            return false;
        }
    }

    // =====================================================
    // GET VERIFIED REQUESTS
    // =====================================================
    public List<AssistanceRequest> getVerifiedRequests() {

        List<AssistanceRequest> requests = new ArrayList<>();

        String sql = """
                SELECT
                    request_id,
                    applicant_name,
                    applicant_address,
                    contact_number,
                    assistance_type_id,
                    request_description,
                    date_submitted,
                    verification_status,
                    verification_remarks,
                    approval_status,
                    approval_remarks,
                    received_by,
                    reviewed_by,
                    approved_by
                FROM assistance_requests
                WHERE verification_status = 'VERIFIED'
                  AND approval_status = 'PENDING'
                ORDER BY request_id ASC
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                requests.add(mapRequest(result));
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error retrieving verified requests: "
                            + e.getMessage()
            );
        }

        return requests;
    }

    // =====================================================
    // APPROVE OR REJECT REQUEST
    // =====================================================
    public boolean approveOrReject(
            int requestId,
            int approvedBy,
            String approvalStatus,
            String approvalRemarks) {

        String sql = """
                UPDATE assistance_requests
                SET
                    approval_status = ?,
                    approval_remarks = ?,
                    approved_by = ?
                WHERE request_id = ?
                  AND verification_status = 'VERIFIED'
                  AND approval_status = 'PENDING'
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, approvalStatus);
            statement.setString(2, approvalRemarks);
            statement.setInt(3, approvedBy);
            statement.setInt(4, requestId);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println(
                    "Approval failed: " + e.getMessage()
            );
            return false;
        }
    }

    // =====================================================
    // UPDATE REQUEST
    // =====================================================
    public boolean updateRequest(
            int requestId,
            String applicantName,
            String applicantAddress,
            String contactNumber,
            int assistanceTypeId,
            String requestDescription) {

        String sql = """
                UPDATE assistance_requests
                SET
                    applicant_name = ?,
                    applicant_address = ?,
                    contact_number = ?,
                    assistance_type_id = ?,
                    request_description = ?
                WHERE request_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, applicantName);
            statement.setString(2, applicantAddress);
            statement.setString(3, contactNumber);
            statement.setInt(4, assistanceTypeId);
            statement.setString(5, requestDescription);
            statement.setInt(6, requestId);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println(
                    "Update request failed: " + e.getMessage()
            );
            return false;
        }
    }

    // =====================================================
    // UPDATE VERIFICATION
    // =====================================================
    public boolean updateVerification(
            int requestId,
            int reviewedBy,
            String verificationStatus,
            String verificationRemarks) {

        return verifyRequest(
                requestId,
                reviewedBy,
                verificationStatus,
                verificationRemarks
        );
    }

    // =====================================================
    // UPDATE APPROVAL
    // =====================================================
    public boolean updateApproval(
            int requestId,
            int approvedBy,
            String approvalStatus,
            String approvalRemarks) {

        return approveOrReject(
                requestId,
                approvedBy,
                approvalStatus,
                approvalRemarks
        );
    }

    // =====================================================
    // DELETE REQUEST
    // =====================================================
    public boolean deleteRequest(int requestId) {

        String sql = """
                DELETE FROM assistance_requests
                WHERE request_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, requestId);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println(
                    "Delete request failed: " + e.getMessage()
            );
            return false;
        }
    }

    // =====================================================
    // GET ASSISTANCE TYPES
    // =====================================================
    public List<String[]> getAssistanceTypes() {

        List<String[]> types = new ArrayList<>();

        String sql = """
                SELECT
                    assistance_type_id,
                    type_name
                FROM assistance_types
                WHERE type_status = 'ACTIVE'
                ORDER BY assistance_type_id
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                types.add(new String[]{
                        String.valueOf(
                                result.getInt("assistance_type_id")
                        ),
                        result.getString("type_name")
                });
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error retrieving assistance types: "
                            + e.getMessage()
            );
        }

        return types;
    }

    // =====================================================
    // SUMMARY REPORT
    // =====================================================
    public void showSummaryReport() {

        String sql = """
                SELECT
                    COUNT(*) AS total_requests,
                    SUM(
                        CASE
                            WHEN verification_status = 'PENDING'
                            THEN 1 ELSE 0
                        END
                    ) AS pending_verification,
                    SUM(
                        CASE
                            WHEN verification_status = 'VERIFIED'
                            THEN 1 ELSE 0
                        END
                    ) AS verified_requests,
                    SUM(
                        CASE
                            WHEN verification_status = 'REJECTED'
                            THEN 1 ELSE 0
                        END
                    ) AS rejected_verification,
                    SUM(
                        CASE
                            WHEN approval_status = 'APPROVED'
                            THEN 1 ELSE 0
                        END
                    ) AS approved_requests,
                    SUM(
                        CASE
                            WHEN approval_status = 'REJECTED'
                            THEN 1 ELSE 0
                        END
                    ) AS rejected_approval,
                    SUM(
                        CASE
                            WHEN approval_status = 'PENDING'
                            THEN 1 ELSE 0
                        END
                    ) AS pending_approval
                FROM assistance_requests
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            if (result.next()) {

                System.out.println();
                System.out.println("==============================================");
                System.out.println("             REQUEST SUMMARY REPORT");
                System.out.println("==============================================");

                System.out.println(
                        "Total Requests        : "
                                + result.getInt("total_requests")
                );

                System.out.println(
                        "Pending Verification  : "
                                + result.getInt("pending_verification")
                );

                System.out.println(
                        "Verified Requests     : "
                                + result.getInt("verified_requests")
                );

                System.out.println(
                        "Rejected Verification : "
                                + result.getInt("rejected_verification")
                );

                System.out.println(
                        "Pending Approval      : "
                                + result.getInt("pending_approval")
                );

                System.out.println(
                        "Approved Requests     : "
                                + result.getInt("approved_requests")
                );

                System.out.println(
                        "Rejected Approval     : "
                                + result.getInt("rejected_approval")
                );

                System.out.println("==============================================");
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error generating summary report: "
                            + e.getMessage()
            );
        }
    }

    // =====================================================
    // MAP DATABASE RESULT TO OBJECT
    // =====================================================
    private AssistanceRequest mapRequest(ResultSet result)
            throws SQLException {

        return new AssistanceRequest(
                result.getInt("request_id"),
                result.getString("applicant_name"),
                result.getString("applicant_address"),
                result.getString("contact_number"),
                result.getInt("assistance_type_id"),
                result.getString("request_description"),
                result.getString("date_submitted"),
                result.getString("verification_status"),
                result.getString("verification_remarks"),
                result.getString("approval_status"),
                result.getString("approval_remarks"),
                getNullableInt(result, "received_by"),
                getNullableInt(result, "reviewed_by"),
                getNullableInt(result, "approved_by")
        );
    }

    // =====================================================
    // HANDLE NULL INTEGER VALUES
    // =====================================================
    private Integer getNullableInt(
            ResultSet result,
            String columnName) throws SQLException {

        int value = result.getInt(columnName);

        if (result.wasNull()) {
            return null;
        }

        return value;
    }
}