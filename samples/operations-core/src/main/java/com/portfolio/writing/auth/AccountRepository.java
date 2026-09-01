package com.portfolio.writing.auth;

import java.util.Optional;

public interface AccountRepository {
    Optional<UserAccount> findById(String userId);

    void save(UserAccount account);
}

