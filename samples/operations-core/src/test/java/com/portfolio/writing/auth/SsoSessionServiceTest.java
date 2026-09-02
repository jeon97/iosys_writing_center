package com.portfolio.writing.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SsoSessionServiceTest {
    private static final Instant NOW = Instant.parse("2026-01-15T00:00:00Z");

    @Test
    void replacesExistingSessionAndKeepsAllowedTarget() {
        MemorySessions sessions = new MemorySessions();
        sessions.active = "old-session";
        var service = service(sessions, NOW.plusSeconds(60));
        var result = service.login(new SsoSessionService.SsoAssertion("payload", "signature"), "/writing/report");
        assertEquals("old-session", sessions.expired);
        assertEquals("/writing/report", result.target());
    }

    @Test
    void fallsBackForUnknownTarget() {
        var result = service(new MemorySessions(), NOW.plusSeconds(60)).login(
                new SsoSessionService.SsoAssertion("payload", "signature"), "https://outside.invalid");
        assertEquals("/writing/home", result.target());
    }

    @Test
    void rejectsExpiredAssertion() {
        assertThrows(SecurityException.class, () -> service(new MemorySessions(), NOW).login(
                new SsoSessionService.SsoAssertion("payload", "signature"), "/writing/home"));
    }

    private SsoSessionService service(MemorySessions sessions, Instant expiresAt) {
        return new SsoSessionService(
                assertion -> Optional.of(new SsoSessionService.VerifiedIdentity(
                        "user-1", SsoSessionService.Role.STUDENT, expiresAt)),
                sessions, Clock.fixed(NOW, ZoneOffset.UTC),
                Set.of("/writing/home", "/writing/report"));
    }

    private static final class MemorySessions implements SsoSessionService.SessionRegistry {
        private String active;
        private String expired;
        public String activeSessionId(String userId) { return active; }
        public void expire(String sessionId) { expired = sessionId; }
        public String create(String userId, SsoSessionService.Role role, Instant at) { active = "new-session"; return active; }
    }
}
