package com.pocketful.service;

import com.pocketful.entity.Account;
import com.pocketful.exception.ConflictException;
import com.pocketful.repository.AccountRepository;
import com.pocketful.web.dto.account.NewAccountDTO;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDate;

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

        return accountRepository.save(
                Account.builder()
                        .name(newAccountDTO.getName())
                        .email(newAccountDTO.getEmail())
                        .phoneNumber(newAccountDTO.getPhoneNumber())
                        .createdAt(LocalDate.now())
                        .updatedAt(LocalDate.now())
                        .build()
        );
    }
}
