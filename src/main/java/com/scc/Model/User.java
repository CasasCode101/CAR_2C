package com.scc.Model;

public record User(int id, String username, String password, String roles, String status) {
    public String toString() {
        return String.format("[%d] %s (%s) - Status: %s", this.id, this.password, this.roles, this.status);
    }
}