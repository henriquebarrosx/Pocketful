package com.pocketful.utils;

import com.pocketful.entity.Account;
import com.pocketful.web.dto.account.NewAccountDTO;

public class AccountBuilder {
    public static Account buildAccount() {
        return Account.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@mail.com")
                .phoneNumber("5582988776655")
                .build();
    }

    public static NewAccountDTO buildNewAccountRequest() {
        return NewAccountDTO.builder()
                .email("john.doe@mail.com")
                .phoneNumber("+5582991880022")
                .name("John Doe")
                .build();
    }
}
