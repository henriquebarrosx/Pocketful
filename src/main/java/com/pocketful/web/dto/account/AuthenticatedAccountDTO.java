package com.pocketful.web.dto.account;

import com.pocketful.enums.AccountRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class AuthenticatedAccountDTO {
    private final Long id;
    private final String name;
    private final String email;
    private final String token;
    private final AccountRole role;
}
