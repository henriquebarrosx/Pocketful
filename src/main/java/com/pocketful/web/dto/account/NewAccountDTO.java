package com.pocketful.web.dto.account;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class NewAccountDTO {
    private final String name;
    private final String email;
    private final String phoneNumber;
}
