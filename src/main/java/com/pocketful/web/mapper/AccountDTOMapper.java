package com.pocketful.web.mapper;

import com.pocketful.entity.Account;
import com.pocketful.web.dto.account.AccountDTO;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class AccountDTOMapper implements Function<Account, AccountDTO> {
    @Override
    public AccountDTO apply(Account account) {
        return AccountDTO.builder()
                .id(account.getId())
                .name(account.getName())
                .email(account.getEmail())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }
}
