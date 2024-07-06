package com.pocketful.controller;

import com.pocketful.entity.Account;
import com.pocketful.service.AccountService;
import com.pocketful.service.TokenService;
import com.pocketful.web.dto.account.AccountAuthDTO;
import com.pocketful.web.dto.account.AccountIdDTO;
import com.pocketful.web.dto.account.AuthenticatedAccountDTO;
import com.pocketful.web.dto.account.NewAccountDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RequestMapping("v1/auth")
@RestController
public class AuthController {
    private final AccountService accountService;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("sign-in")
    ResponseEntity<AuthenticatedAccountDTO> signIn(@RequestBody AccountAuthDTO request) {
        AuthenticatedAccountDTO account = authenticate(request);
        return ResponseEntity.status(HttpStatus.OK).body(account);
    }

    public AuthenticatedAccountDTO authenticate(AccountAuthDTO request) {
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

    @PostMapping("sign-up")
    ResponseEntity<AccountIdDTO> signUp(@RequestBody NewAccountDTO request) {
        Account account = accountService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AccountIdDTO(account.getId()));
    }
}
