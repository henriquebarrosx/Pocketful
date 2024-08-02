package com.pocketful.utils;

import com.pocketful.web.dto.account.SignUpRequestDTO;

public class NewAccountRequestBuilder {
    public static SignUpRequestDTO build() {
        return SignUpRequestDTO.builder()
                .name("John Doe")
                .email("john.doe@mail.com")
                .phoneNumber("+5582988991100")
                .password("12345678")
                .build();
    }
}
