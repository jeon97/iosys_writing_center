package com.portfolio.writing.auth;

import java.time.Clock;
import java.time.Instant;
import java.util.Set;

public final class SsoSessionService {
    private final AssertionVerifier verifier;
    private final SessionRegistry sessions;
    private final Clock clock;
    private final Set<String> allowedTargets;

    public SsoSessionService(AssertionVerifier verifier, SessionRegistry sessions,
                             Clock clock, Set<String> allowedTargets) {
        this.verifier = verifier;
        this.sessions = sessions;
        this.clock = clock;
        this.allowedTargets = Set.copyOf(allowedTargets);
    }

    public LoginResult login(SsoAssertion assertion, String requestedTarget) {
        VerifiedIdentity identity = verifier.verify(assertion)
                .orElseThrow(() -> new SecurityException("invalid SSO assertion"));
        if (!clock.instant().isBefore(identity.expiresAt())) {
            throw new SecurityException("expired SSO assertion");
        }
        String target = allowedTargets.contains(requestedTarget)
                ? requestedTarget : defaultTarget(identity.role());
        String replacedSessionId = sessions.activeSessionId(identity.userId());
        if (replacedSessionId != null) sessions.expire(replacedSessionId);
        String sessionId = sessions.create(identity.userId(), identity.role(), clock.instant());
        return new LoginResult(sessionId, target, replacedSessionId);
    }

    public void logout(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) return;
        sessions.expire(sessionId);
    }

    private String defaultTarget(Role role) {
        return role == Role.ADMIN ? "/manager/home" : "/writing/home";
    }

    public enum Role { STUDENT, STAFF, ADMIN }
    public record SsoAssertion(String payload, String signature) {}
    public record VerifiedIdentity(String userId, Role role, Instant expiresAt) {}
    public record LoginResult(String sessionId, String target, String replacedSessionId) {}

    public interface AssertionVerifier {
        java.util.Optional<VerifiedIdentity> verify(SsoAssertion assertion);
    }

    public interface SessionRegistry {
        String activeSessionId(String userId);
        void expire(String sessionId);
        String create(String userId, Role role, Instant createdAt);
    }
}
