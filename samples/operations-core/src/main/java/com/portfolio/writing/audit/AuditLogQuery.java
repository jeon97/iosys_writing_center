package com.portfolio.writing.audit;

import java.time.Instant;

public record AuditLogQuery(String userId, Instant from, Instant to) {
    public AuditLogQuery {
        if (from == null || to == null || from.isAfter(to)) {
            throw new IllegalArgumentException("valid search period is required");
        }
    }
}

