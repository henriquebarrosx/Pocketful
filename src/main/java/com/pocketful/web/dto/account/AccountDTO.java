package com.pocketful.web.dto.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class AccountDTO {
    private final Long id;
    private final String name;
    private final String email;
    private final String phoneNumber;
    private final LocalDate createdAt;
    private final LocalDate updatedAt;
}
