package com.pocketful.service;

import com.pocketful.entity.Account;
import com.pocketful.exception.BadRequestException;
import com.pocketful.exception.ConflictException;
import com.pocketful.exception.NotFoundException;
import com.pocketful.repository.AccountRepository;
import com.pocketful.web.dto.account.NewAccountDTO;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDate;
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
                        .createdAt(LocalDate.now())
                        .updatedAt(LocalDate.now())
                        .build()
        );
    }

    public Account findById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account id do not exist."));
    }

    boolean isValidPhoneNumber(String phoneNumber) {
        Pattern pattern = Pattern.compile("^\\+55(\\d{11})$");
        return pattern.matcher(phoneNumber).matches();
    }
}
