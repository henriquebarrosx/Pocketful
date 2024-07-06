package com.pocketful.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.pocketful.entity.Account;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenService {
    @Value("${security.token}")
    private String secret;

    public String generateToken(Account account) {
        Algorithm algorithm = Algorithm.HMAC256(secret);

        try {
            return JWT.create()
                .withIssuer("auth0")
                .withSubject(account.getEmail())
                .sign(algorithm);
        } catch (Exception exception) {
            throw new RuntimeException("Something wrong at JWT token generation", exception);
        }
    }

    public String decodeToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secret);

        return JWT.require(algorithm)
            .withIssuer("auth0")
            .build()
            .verify(token)
            .getSubject();
    }
}
