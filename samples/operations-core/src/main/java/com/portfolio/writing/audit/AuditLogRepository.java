package com.portfolio.writing.audit;

import java.time.Instant;
import java.util.List;

public interface AuditLogRepository {
    List<AuditLog> find(AuditLogQuery query, int offset, int limit);

    long count(AuditLogQuery query);

    List<AuditLog> findAll(AuditLogQuery query);

    int deleteBefore(Instant boundary);
}

