package com.portfolio.writing.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

class AuthenticationServiceTest {
    @Test
    void resetsFailuresAndReplacesExistingSession() {
        MemoryAccounts accounts = new MemoryAccounts();
        accounts.save(new UserAccount("student-1", UserAccount.Role.STUDENT, true, 2, false));
        AtomicReference<String> session = new AtomicReference<>();
        AuthenticationService service = new AuthenticationService(
                accounts, (id, password) -> true, (id, sessionId) -> session.set(sessionId)
        );

        LoginResult result = service.login("student-1", "valid".toCharArray(), "session-new");

        assertEquals(LoginResult.Destination.STUDENT_HOME, result.destination());
        assertEquals(0, accounts.findById("student-1").orElseThrow().failedAttempts());
        assertEquals("session-new", session.get());
    }

    @Test
    void incrementsFailureCountWhenProviderRejectsCredentials() {
        MemoryAccounts accounts = new MemoryAccounts();
        accounts.save(new UserAccount("student-1", UserAccount.Role.STUDENT, true, 1, false));
        AuthenticationService service = new AuthenticationService(
                accounts, (id, password) -> false, (id, sessionId) -> { }
        );

        assertThrows(AuthenticationService.LoginFailedException.class,
                () -> service.login("student-1", "invalid".toCharArray(), "session-new"));
        assertEquals(2, accounts.findById("student-1").orElseThrow().failedAttempts());
    }

    private static final class MemoryAccounts implements AccountRepository {
        private final Map<String, UserAccount> values = new HashMap<>();

        @Override
        public Optional<UserAccount> findById(String userId) {
            return Optional.ofNullable(values.get(userId));
        }

        @Override
        public void save(UserAccount account) {
            values.put(account.userId(), account);
        }
    }
}

