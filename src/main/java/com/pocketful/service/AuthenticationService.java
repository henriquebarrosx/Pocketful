package com.pocketful.service;

import com.pocketful.entity.Account;
import com.pocketful.util.JsonWebToken;
import com.pocketful.model.dto.account.AuthenticatedAccountDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthenticationService {
    private final JsonWebToken jsonWebToken;
    private final AuthenticationManager authenticationManager;

    public AuthenticatedAccountDTO authenticate(String email, String password) {
        var credentials = new UsernamePasswordAuthenticationToken(email, password);
        var authentication = authenticationManager.authenticate(credentials);
        Account account = (Account) authentication.getPrincipal();
        String token = jsonWebToken.encode(account.getEmail());
        return this.getAuthenticatedAccountDTO(account, token);
    }

    private AuthenticatedAccountDTO getAuthenticatedAccountDTO(Account account, String token) {
        return AuthenticatedAccountDTO.builder()
                .id(account.getId())
                .name(account.getName())
                .email(account.getEmail())
                .role(account.getRole())
                .token(token)
                .build();
    }
}
