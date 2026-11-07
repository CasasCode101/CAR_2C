package com.scc.Model;

public record ActivityLog(
        int logId,
        int userId,
        String actionType,
        int recordId,
        String description,
        String timestamp
) {
}