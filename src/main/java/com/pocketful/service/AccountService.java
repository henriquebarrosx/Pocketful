package com.pocketful.service;

import com.pocketful.entity.Account;
import com.pocketful.entity.AccountRole;
import com.pocketful.exception.BadRequestException;
import com.pocketful.exception.ConflictException;
import com.pocketful.exception.NotFoundException;
import com.pocketful.repository.AccountRepository;
import com.pocketful.web.dto.account.NewAccountDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@AllArgsConstructor
@Service
public class AccountService {
    private final AccountRepository accountRepository;

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public Account create(NewAccountDTO request) {
        Boolean existsAccountByEmailOrPhoneNumber = accountRepository
                .existsAccountByEmailOrPhoneNumber(request.getEmail(), request.getPhoneNumber());

        if (existsAccountByEmailOrPhoneNumber) {
            log.error("Failed creating account using existent email and/or phone number: {} {}", request.getEmail(), request.getPhoneNumber());
            throw new ConflictException("An account with this email or phone number already exists.");
        }

        if (!isValidPhoneNumber(request.getPhoneNumber())) {
            log.error("Failed creating account using invalid phone number: {}", request.getPhoneNumber());
            throw new BadRequestException("Invalid account phone number. Please, provide valid country (Ex.: +55) and DDD number.");
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

    public Account findById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> {
                    log.error("Account not found: id - {}", accountId);
                    return new NotFoundException("Account not found");
                });
    }

    public Account findByEmail(String email) {
        return accountRepository.findAccountByEmail(email)
            .orElseThrow(() -> {
                log.error("Account email not found: {}", email);
                return new NotFoundException("Account not found");
            });
    }

    boolean isValidPhoneNumber(String phoneNumber) {
        Pattern pattern = Pattern.compile("^\\+55(\\d{11})$");
        return pattern.matcher(phoneNumber).matches();
    }
}
