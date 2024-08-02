package com.pocketful.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public abstract class PasswordEncoder {
    public static String encode(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }
}
