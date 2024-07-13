package com.pocketful.utils;

import com.pocketful.web.dto.account.NewAccountRequestDTO;

public class NewAccountRequestBuilder {
    public static NewAccountRequestDTO build() {
        return NewAccountRequestDTO.builder()
                .name("John Doe")
                .email("john.doe@mail.com")
                .phoneNumber("+5582988991100")
                .password("12345678")
                .build();
    }
}
