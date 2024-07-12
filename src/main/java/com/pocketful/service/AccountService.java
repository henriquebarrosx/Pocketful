package com.pocketful.service;

import com.pocketful.entity.Account;
import com.pocketful.entity.AccountRole;
import com.pocketful.exception.AccountNotFoundException;
import com.pocketful.exception.EmailOrPhoneNumberAlreadyExistException;
import com.pocketful.exception.InvalidPhoneNumberException;
import com.pocketful.repository.AccountRepository;
import com.pocketful.web.dto.account.NewAccountDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Slf4j
@AllArgsConstructor
@Service
public class AccountService {
    private final AccountRepository accountRepository;

    public Account create(NewAccountDTO request) {
        Boolean existsAccountByEmailOrPhoneNumber = accountRepository
                .existsAccountByEmailOrPhoneNumber(request.getEmail(), request.getPhoneNumber());

        if (existsAccountByEmailOrPhoneNumber) {
            throw new EmailOrPhoneNumberAlreadyExistException(request.getEmail(), request.getPhoneNumber());
        }

        if (!isValidPhoneNumber(request.getPhoneNumber())) {
            throw new InvalidPhoneNumberException();
        }

        return accountRepository.save(
                Account.builder()
                        .name(request.getName())
                        .email(request.getEmail())
                        .phoneNumber(request.getPhoneNumber())
                        .password(encodePassword(request.getPassword()))
                        .role(AccountRole.DEFAULT)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        );
    }

    private String encodePassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }

    public Account findById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }

    public Account findByEmail(String email) {
        return accountRepository.findAccountByEmail(email)
            .orElseThrow(() -> new AccountNotFoundException(email));
    }

    boolean isValidPhoneNumber(String phoneNumber) {
        Pattern pattern = Pattern.compile("^\\+55(\\d{11})$");
        return pattern.matcher(phoneNumber).matches();
    }
}
