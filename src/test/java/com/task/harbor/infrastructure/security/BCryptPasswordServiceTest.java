package com.task.harbor.infrastructure.security;

import com.task.harbor.infrastructure.security.password.BCryptPasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BCryptPasswordServiceTest {

    private BCryptPasswordService passwordService;

    @BeforeEach
    void setUp() {
        passwordService = new BCryptPasswordService(12);
    }

    @Test
    void hashPassword_ReturnsNonNullHash() {
        String password = "testPassword123";

        String hash = passwordService.hashPassword(password);

        assertNotNull(hash);
        assertNotEquals(password, hash);
    }

    @Test
    void hashPassword_DifferentHashesForSamePassword() {
        String password = "testPassword123";

        String hash1 = passwordService.hashPassword(password);
        String hash2 = passwordService.hashPassword(password);

        assertNotEquals(hash1, hash2);
    }

    @Test
    void verifyPassword_ReturnsTrueForCorrectPassword() {
        String password = "testPassword123";
        String hash = passwordService.hashPassword(password);

        boolean result = passwordService.verifyPassword(password, hash);

        assertTrue(result);
    }

    @Test
    void verifyPassword_ReturnsFalseForWrongPassword() {
        String password = "testPassword123";
        String wrongPassword = "wrongPassword";
        String hash = passwordService.hashPassword(password);

        boolean result = passwordService.verifyPassword(wrongPassword, hash);

        assertFalse(result);
    }

    @Test
    void verifyPassword_ReturnsFalseForInvalidHash() {
        String password = "testPassword123";
        String invalidHash = "invalidHash";

        boolean result = passwordService.verifyPassword(password, invalidHash);

        assertFalse(result);
    }
}