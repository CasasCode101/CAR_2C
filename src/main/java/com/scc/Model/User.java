package com.scc.Model;

public record User(int id, String username, String password, String role, String status) {

    @Override
    public String toString() {
        return String.format(
                "[ID: %d] Username: %s | Role: %s | Status: %s",
                id, username, role, status
        );
    }
}