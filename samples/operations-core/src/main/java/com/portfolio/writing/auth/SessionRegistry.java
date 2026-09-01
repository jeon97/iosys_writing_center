package com.portfolio.writing.auth;

public interface SessionRegistry {
    void replace(String userId, String sessionId);
}

