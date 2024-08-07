package com.pocketful.web.dto.account;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequestDTO {
    private String name;
    private String email;
    private String password;
}
