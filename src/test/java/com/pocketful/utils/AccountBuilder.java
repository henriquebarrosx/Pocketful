package com.pocketful.utils;

import com.pocketful.entity.Account;
import com.pocketful.enums.AccountRole;

import java.time.LocalDateTime;

public class AccountBuilder {
    public static Account build() {
        return Account.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@mail.com")
                .password("LOREMARIE")
                .role(AccountRole.ADMIN)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static Account buildWithId(Long id) {
        return Account.builder()
                .id(id)
                .name("John Doe")
                .email("john.doe@mail.com")
                .password("LOREMARIE")
                .role(AccountRole.ADMIN)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static Account build(AccountRole role) {
        return Account.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@mail.com")
                .password("LOREMARIE")
                .role(role)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
