package com.portfolio.writing.auth;

public record LoginResult(String userId, UserAccount.Role role, Destination destination) {
    public enum Destination {
        CONSENT, STUDENT_HOME, TUTOR_HOME, ADMIN_HOME
    }
}

