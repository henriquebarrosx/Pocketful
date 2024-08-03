package com.pocketful.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public abstract class JsonWebToken {
    public static final String ISSUER = "auth0";
    private static final Map<String, String> tokensBySubject = new HashMap<>();

    @Value("${security.token}")
    private static String secret = "DEFAULT_SECRET";

    public static String generate(String param) {
        Algorithm algorithm = Algorithm.HMAC256(secret);

        String token = JWT.create()
            .withIssuer(ISSUER)
            .withIssuedAt(getIssuedAt())
            .withSubject(param)
            .sign(algorithm);

        tokensBySubject.put(param, token);
        return token;
    }

    public static String decode(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secret);

        return JWT.require(algorithm)
            .withIssuer(ISSUER)
            .build()
            .verify(sanitize(token))
            .getSubject();
    }

    public static Boolean validate(String subject, String token) {
        String accessToken = tokensBySubject.get(subject);
        return Objects.nonNull(accessToken) && accessToken.equals(token);
    }

    public static void invalidate(String subject) {
        tokensBySubject.put(subject, "");
    }

    public static String sanitize(String token) {
        return token.replace("Bearer ", "");
    }

    private static Instant getIssuedAt() {
        ZoneId zone = ZoneId.of("America/Sao_Paulo");
        return ZonedDateTime.now(zone).toInstant();
    }
}
