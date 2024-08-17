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
    private final AccountService accountService;
    private final AuthenticationManager authenticationManager;
    private final SessionManagerService sessionManagerService;

    public AuthenticatedAccountDTO authenticate(String email, String password) {
        var credentials = new UsernamePasswordAuthenticationToken(email, password);
        var authentication = authenticationManager.authenticate(credentials);
        Account account = (Account) authentication.getPrincipal();

        String token = sessionManagerService.init(account.getEmail());
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
