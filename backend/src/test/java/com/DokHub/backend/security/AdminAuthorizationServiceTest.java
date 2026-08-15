package com.DokHub.backend.security;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AdminAuthorizationServiceTest {

    @Test
    void acceptsMatchingKey() {
        AdminAuthorizationService service = new AdminAuthorizationService("test-key");
        assertDoesNotThrow(() -> service.requireAuthorized("test-key"));
    }

    @Test
    void rejectsMissingOrWrongKey() {
        AdminAuthorizationService service = new AdminAuthorizationService("test-key");
        assertThrows(ResponseStatusException.class, () -> service.requireAuthorized(null));
        assertThrows(ResponseStatusException.class, () -> service.requireAuthorized("wrong-key"));
    }

    @Test
    void rejectsRequestsWhenServerKeyIsNotConfigured() {
        AdminAuthorizationService service = new AdminAuthorizationService("");
        assertThrows(ResponseStatusException.class, () -> service.requireAuthorized("anything"));
    }
}
