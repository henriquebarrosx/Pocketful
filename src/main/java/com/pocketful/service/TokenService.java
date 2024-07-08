package com.pocketful.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.pocketful.entity.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Service
public class TokenService {
    private final AccountService accountService;

    private final String ISSUER = "auth0";
    private final Map<String, String> tokensBySubject = new HashMap<>();

    @Value("${security.token}")
    private String secret;

    public String generateToken(Account account) {
        Algorithm algorithm = Algorithm.HMAC256(secret);

        try {
            String token = JWT.create()
                .withIssuer(ISSUER)
                .withIssuedAt(getIssuedAt())
                .withSubject(account.getEmail())
                .sign(algorithm);

            tokensBySubject.put(account.getEmail(), token);
            return token;
        } catch (Exception exception) {
            throw new RuntimeException("Something wrong at JWT token generation", exception);
        }
    }

    public Account decodeToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secret);

        String email = JWT.require(algorithm)
            .withIssuer(ISSUER)
            .build()
            .verify(token)
            .getSubject();

        return accountService.findByEmail(email);
    }

    public Boolean validateToken(String subject, String token) {
        String accessToken = tokensBySubject.get(subject);
        return accessToken.equals(token);
    }

    public void invalidateToken(String subject) {
        tokensBySubject.put(subject, "");
    }

    private Instant getIssuedAt() {
        ZoneId zone = ZoneId.of("America/Sao_Paulo");
        return ZonedDateTime.now(zone).toInstant();
    }
}
