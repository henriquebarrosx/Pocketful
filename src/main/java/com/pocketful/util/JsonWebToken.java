package com.pocketful.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
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
            .withSubject(subject)
            .withIssuedAt(getIssuedAt())
            .withExpiresAt(getExpireAt())
            .sign(algorithm);
    }

    public static String decode(String token) throws JWTVerificationException {
        Algorithm algorithm = Algorithm.HMAC256(secret);

        return JWT.require(algorithm)
            .withIssuer(ISSUER)
            .build()
            .verify(sanitize(token))
            .getSubject();
    }

    public static Boolean validate(String token) {
        try {
            decode(token);
            return true;
        } catch (JWTVerificationException e) {
            log.warn("invalid token: {}", e.getMessage());
            return false;
        }
    }

    public static String sanitize(String token) {
        return token.replace("Bearer ", "");
    }

    private static Instant getIssuedAt() {
        ZoneId zone = ZoneId.of("America/Sao_Paulo");
        return ZonedDateTime.now(zone).toInstant();
    }

    private static Instant getExpireAt() {
        ZoneId zone = ZoneId.of("America/Sao_Paulo");
        return ZonedDateTime.now(zone).plusWeeks(1).toInstant();
    }

}
