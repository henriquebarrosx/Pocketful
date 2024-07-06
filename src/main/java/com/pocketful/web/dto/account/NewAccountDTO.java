package com.pocketful.web.dto.account;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewAccountDTO {
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
}
