package com.pocketful.service;

import com.pocketful.util.JsonWebToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class SessionManagerService {
    private static final Map<String, String> inMemorySessions = new HashMap<>();

    public String init(String subject) {
        String token = JsonWebToken.generate(subject);
        inMemorySessions.put(subject, token);
        return token;
    }

    public static Boolean validate(String subject, String token) {
        String accessToken = inMemorySessions.get(subject);
        return Objects.nonNull(accessToken) && accessToken.equals(token);
    }

    public static void invalidate(String subject) {
        inMemorySessions.remove(subject);
    }
}
