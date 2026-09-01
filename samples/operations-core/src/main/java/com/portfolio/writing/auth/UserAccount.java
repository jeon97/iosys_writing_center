package com.portfolio.writing.auth;

public record UserAccount(
        String userId,
        Role role,
        boolean active,
        int failedAttempts,
        boolean consentRequired
) {
    public enum Role {
        STUDENT, TUTOR, ADMIN
    }
}

