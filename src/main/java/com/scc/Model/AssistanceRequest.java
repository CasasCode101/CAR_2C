package com.scc.Model;

public record AssistanceRequest(
        int requestId,
        String applicantName,
        String applicantAddress,
        String contactNumber,
        int assistanceTypeId,
        String requestDescription,
        String dateSubmitted,
        String verificationStatus,
        String verificationRemarks,
        String approvalStatus,
        String approvalRemarks,
        Integer receivedBy,
        Integer reviewedBy,
        Integer approvedBy
) {
}