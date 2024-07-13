package com.pocketful.service;

import com.pocketful.entity.Account;
import com.pocketful.web.dto.account.AuthenticatedAccountDTO;
import com.pocketful.web.dto.account.SignInRequestDTO;
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

    public AuthenticatedAccountDTO authenticate(SignInRequestDTO request) {
        var credentials = new UsernamePasswordAuthenticationToken(request.email(), request.password());
        var auth = authenticationManager.authenticate(credentials);

        String token = tokenService.generateToken((Account) auth.getPrincipal());
        Account account = accountService.findByEmail(request.email());

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
