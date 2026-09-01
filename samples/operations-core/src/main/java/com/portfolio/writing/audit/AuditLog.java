package com.portfolio.writing.audit;

import java.time.Instant;

public record AuditLog(String userId, String action, Instant occurredAt) {
}

