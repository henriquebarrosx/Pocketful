package com.pocketful.utils;

import com.pocketful.entity.AccountRole;
import com.pocketful.web.dto.account.AuthenticatedAccountDTO;

public class AuthenticatedAccountBuilder {
    public static AuthenticatedAccountDTO build() {
        return AuthenticatedAccountDTO.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@mail.com")
                .role(AccountRole.ADMIN)
                .token("00000000000000000")
                .build();
    }
}
