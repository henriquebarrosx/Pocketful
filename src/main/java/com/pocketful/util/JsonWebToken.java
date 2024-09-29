package com.pocketful.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

@Component
@Slf4j
public class JsonWebToken {

    @Value("${jwt.secret}")
    private String secret = "DEFAULT";

    @Value("${jwt.lifetime_in_hours}")
    private String sessionExpirationTime = "12";

    private final String issuer = "auth0";

    public String encode(String subject) {
        Algorithm algorithm = Algorithm.HMAC256(secret);

        return JWT.create()
            .withIssuer(issuer)
            .withSubject(subject)
            .withIssuedAt(getIssuedAt())
            .withExpiresAt(getExpireAt())
            .sign(algorithm);
    }

    public Optional<String> decode(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            String decodedSubject = JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build()
                    .verify(sanitize(token))
                    .getSubject();

            return Optional.of(decodedSubject);
        }

        catch (JWTVerificationException exception) {
            log.error(exception.getMessage());
            return Optional.empty();
        }
    }

    public String sanitize(String token) {
        return token.replace("Bearer ", "");
    }

    private Instant getIssuedAt() {
        ZoneId zone = ZoneId.of("America/Sao_Paulo");
        return ZonedDateTime.now(zone).toInstant();
    }

    private Instant getExpireAt() {
        ZoneId zone = ZoneId.of("America/Sao_Paulo");
        long expirationTime = Long.parseLong(sessionExpirationTime);
        return ZonedDateTime.now(zone).plusHours(expirationTime).toInstant();
    }

}
