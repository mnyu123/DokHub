package com.DokHub.backend.config;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvLoader {
    private static final Dotenv dotenv = Dotenv.configure().load();

    public static String get(String key) {
        return dotenv.get(key);
    }
}
