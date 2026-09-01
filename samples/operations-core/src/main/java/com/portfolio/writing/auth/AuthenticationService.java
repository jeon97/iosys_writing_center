package com.portfolio.writing.auth;

public final class AuthenticationService {
    private static final int MAX_FAILED_ATTEMPTS = 5;

    private final AccountRepository accounts;
    private final AuthenticationProvider provider;
    private final SessionRegistry sessions;

    public AuthenticationService(
            AccountRepository accounts,
            AuthenticationProvider provider,
            SessionRegistry sessions
    ) {
        this.accounts = accounts;
        this.provider = provider;
        this.sessions = sessions;
    }

    public LoginResult login(String userId, char[] password, String newSessionId) {
        UserAccount account = accounts.findById(userId)
                .orElseThrow(() -> new LoginFailedException("invalid credentials"));

        if (!account.active()) {
            throw new LoginFailedException("account is inactive");
        }
        if (account.failedAttempts() >= MAX_FAILED_ATTEMPTS) {
            throw new LoginFailedException("account is locked");
        }
        if (!provider.authenticate(userId, password)) {
            accounts.save(copyWithFailedAttempts(account, account.failedAttempts() + 1));
            throw new LoginFailedException("invalid credentials");
        }

        accounts.save(copyWithFailedAttempts(account, 0));
        sessions.replace(userId, newSessionId);
        return new LoginResult(userId, account.role(), destinationOf(account));
    }

    private LoginResult.Destination destinationOf(UserAccount account) {
        if (account.consentRequired()) {
            return LoginResult.Destination.CONSENT;
        }
        return switch (account.role()) {
            case STUDENT -> LoginResult.Destination.STUDENT_HOME;
            case TUTOR -> LoginResult.Destination.TUTOR_HOME;
            case ADMIN -> LoginResult.Destination.ADMIN_HOME;
        };
    }

    private UserAccount copyWithFailedAttempts(UserAccount account, int failedAttempts) {
        return new UserAccount(
                account.userId(), account.role(), account.active(),
                failedAttempts, account.consentRequired()
        );
    }

    public static final class LoginFailedException extends RuntimeException {
        public LoginFailedException(String message) {
            super(message);
        }
    }
}

