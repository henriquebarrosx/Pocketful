package com.pocketful.service;

import com.pocketful.entity.Account;
import com.pocketful.web.dto.account.AuthenticatedAccountDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthenticationService {
    private final TokenService tokenService;
    private final AccountService accountService;
    private final AuthenticationManager authenticationManager;

    public AuthenticatedAccountDTO authenticate(String email, String password) {
        var credentials = new UsernamePasswordAuthenticationToken(email, password);
        var auth = authenticationManager.authenticate(credentials);

        String token = tokenService.generateToken((Account) auth.getPrincipal());
        Account account = accountService.findByEmail(email);

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
