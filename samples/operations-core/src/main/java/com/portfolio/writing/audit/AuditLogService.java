package com.portfolio.writing.audit;

import java.time.Clock;
import java.time.Duration;
import java.util.List;

public final class AuditLogService {
    private final AuditLogRepository repository;
    private final Clock clock;

    public AuditLogService(AuditLogRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    public Page search(AuditLogQuery query, int page, int size) {
        if (page < 0 || size < 1 || size > 500) {
            throw new IllegalArgumentException("invalid page request");
        }
        return new Page(repository.find(query, page * size, size), repository.count(query));
    }

    public List<AuditLog> export(AuditLogQuery query) {
        return List.copyOf(repository.findAll(query));
    }

    public int deleteExpired(Duration retention, boolean administrator) {
        if (!administrator) {
            throw new SecurityException("administrator role is required");
        }
        if (retention.isNegative() || retention.isZero()) {
            throw new IllegalArgumentException("retention must be positive");
        }
        return repository.deleteBefore(clock.instant().minus(retention));
    }

    public record Page(List<AuditLog> content, long totalElements) {
        public Page {
            content = List.copyOf(content);
        }
    }
}

