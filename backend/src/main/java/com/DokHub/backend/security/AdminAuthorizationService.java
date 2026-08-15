package com.DokHub.backend.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Service
public class AdminAuthorizationService {

    private final byte[] configuredKey;

    public AdminAuthorizationService(@Value("${app.admin.api-key:}") String configuredKey) {
        this.configuredKey = configuredKey == null
                ? new byte[0]
                : configuredKey.trim().getBytes(StandardCharsets.UTF_8);
    }

    public void requireAuthorized(String providedKey) {
        if (configuredKey.length == 0) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "관리 API 키가 설정되지 않았습니다.");
        }

        byte[] provided = providedKey == null
                ? new byte[0]
                : providedKey.trim().getBytes(StandardCharsets.UTF_8);
        if (!MessageDigest.isEqual(configuredKey, provided)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 관리 API 키입니다.");
        }
    }
}
