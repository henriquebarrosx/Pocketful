package com.pocketful.utils;

import com.pocketful.web.dto.account.SignUpRequestDTO;

public class SignUpRequestBuilder {
    public static SignUpRequestDTO build() {
        return SignUpRequestDTO.builder()
                .name("John Doe")
                .email("john.doe@mail.com")
                .password("12345678")
                .build();
    }
}
