package com.pocketful.service;

import com.pocketful.entity.Account;
import com.pocketful.entity.AccountRole;
import com.pocketful.exception.BadRequestException;
import com.pocketful.exception.ConflictException;
import com.pocketful.exception.NotFoundException;
import com.pocketful.repository.AccountRepository;
import com.pocketful.web.dto.account.NewAccountDTO;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

@AllArgsConstructor
@Service
public class AccountService {
    private final AccountRepository accountRepository;

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public Account create(NewAccountDTO newAccountDTO) {
        Boolean existsAccountByEmailOrPhoneNumber = accountRepository
                .existsAccountByEmailOrPhoneNumber(newAccountDTO.getEmail(), newAccountDTO.getPhoneNumber());

        if (existsAccountByEmailOrPhoneNumber) {
            throw new ConflictException("An account with this email or phone number already exists.");
        }

        if (!isValidPhoneNumber(newAccountDTO.getPhoneNumber())) {
            throw new BadRequestException("Invalid account phone number. Please, provide valid country (Ex.: +55) and DDD number.");
        }

        return accountRepository.save(
                Account.builder()
                        .name(newAccountDTO.getName())
                        .email(newAccountDTO.getEmail())
                        .phoneNumber(newAccountDTO.getPhoneNumber())
                        .password(encodePassword(newAccountDTO.getPassword()))
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
                .orElseThrow(() -> new NotFoundException("Account id do not exist."));
    }

    public Account findByEmail(String email) {
        return accountRepository.findAccountByEmail(email)
            .orElseThrow(() -> new NotFoundException(String.format("No account found using email %s", email)));
    }

    boolean isValidPhoneNumber(String phoneNumber) {
        Pattern pattern = Pattern.compile("^\\+55(\\d{11})$");
        return pattern.matcher(phoneNumber).matches();
    }
}
