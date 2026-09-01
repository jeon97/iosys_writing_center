package com.portfolio.writing.auth;

public interface AuthenticationProvider {
    boolean authenticate(String userId, char[] password);
}

