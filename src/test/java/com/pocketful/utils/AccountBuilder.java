package com.pocketful.utils;

import com.pocketful.entity.Account;

public class AccountBuilder {
    public static Account buildAccount() {
        return Account.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@mail.com")
                .phoneNumber("5582988776655")
                .build();
    }
}
