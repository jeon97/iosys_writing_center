package com.portfolio.writing.audit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;

class AuditLogServiceTest {
    private static final Instant NOW = Instant.parse("2026-01-15T00:00:00Z");

    @Test
    void usesSameQueryForSearchAndExport() {
        RecordingRepository repository = new RecordingRepository();
        AuditLogService service = new AuditLogService(repository, Clock.fixed(NOW, ZoneOffset.UTC));
        AuditLogQuery query = new AuditLogQuery("user-1", NOW.minusSeconds(3600), NOW);

        service.search(query, 0, 20);
        assertSame(query, repository.lastQuery);

        service.export(query);
        assertSame(query, repository.lastQuery);
    }

    @Test
    void requiresAdministratorToDeleteExpiredLogs() {
        RecordingRepository repository = new RecordingRepository();
        AuditLogService service = new AuditLogService(repository, Clock.fixed(NOW, ZoneOffset.UTC));

        assertThrows(SecurityException.class,
                () -> service.deleteExpired(Duration.ofDays(365), false));
        assertEquals(0, repository.deleteCalls);

        assertEquals(3, service.deleteExpired(Duration.ofDays(365), true));
        assertEquals(NOW.minus(Duration.ofDays(365)), repository.boundary);
    }

    private static final class RecordingRepository implements AuditLogRepository {
        private AuditLogQuery lastQuery;
        private Instant boundary;
        private int deleteCalls;

        @Override
        public List<AuditLog> find(AuditLogQuery query, int offset, int limit) {
            lastQuery = query;
            return List.of();
        }

        @Override
        public long count(AuditLogQuery query) {
            lastQuery = query;
            return 0;
        }

        @Override
        public List<AuditLog> findAll(AuditLogQuery query) {
            lastQuery = query;
            return List.of();
        }

        @Override
        public int deleteBefore(Instant boundary) {
            this.boundary = boundary;
            deleteCalls++;
            return 3;
        }
    }
}

