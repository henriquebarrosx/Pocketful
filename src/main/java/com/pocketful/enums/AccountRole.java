package com.pocketful.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountRole {
    ADMIN("0"),
    DEFAULT("1");

    private final String role;
}
