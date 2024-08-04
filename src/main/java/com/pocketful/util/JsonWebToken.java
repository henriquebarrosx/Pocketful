package com.pocketful.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Slf4j
public abstract class JsonWebToken {

    @Value("${security.token}")
    private static String secret = "DEFAULT_SECRET";

    public static final String ISSUER = "auth0";

    public static String generate(String subject) {
        Algorithm algorithm = Algorithm.HMAC256(secret);

        return JWT.create()
            .withIssuer(ISSUER)
            .withIssuedAt(getIssuedAt())
            .withSubject(subject)
            .sign(algorithm);
    }

    public static String decode(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secret);

        return JWT.require(algorithm)
            .withIssuer(ISSUER)
            .build()
            .verify(sanitize(token))
            .getSubject();
    }

    public static String sanitize(String token) {
        return token.replace("Bearer ", "");
    }

    private static Instant getIssuedAt() {
        ZoneId zone = ZoneId.of("America/Sao_Paulo");
        return ZonedDateTime.now(zone).toInstant();
    }
}
