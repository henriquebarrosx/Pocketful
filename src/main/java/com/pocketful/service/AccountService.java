package com.pocketful.service;

import com.pocketful.entity.Account;
import com.pocketful.enums.AccountRole;
import com.pocketful.exception.Account.AccountEmailAlreadyRegisteredException;
import com.pocketful.exception.Account.AccountNotFoundException;
import com.pocketful.exception.Account.InvalidAccountEmailFormatException;
import com.pocketful.repository.AccountRepository;
import com.pocketful.util.EmailValidator;
import com.pocketful.util.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class AccountService {
    private final AccountRepository accountRepository;

    public Account create(String name, String email, String password) {
        if (Boolean.FALSE.equals(EmailValidator.validate(email))) {
            throw new InvalidAccountEmailFormatException();
        }

        if (accountRepository.existsByEmail(email)) {
            throw new AccountEmailAlreadyRegisteredException();
        }

        return accountRepository.save(
                Account.builder()
                        .name(name)
                        .email(email)
                        .password(PasswordEncoder.encode(password))
                        .role(AccountRole.DEFAULT)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        );
    }

    public Account findById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }

    public Account findByEmail(String email) {
        return accountRepository.findByEmail(email)
            .orElseThrow(() -> new AccountNotFoundException(email));
    }
}
