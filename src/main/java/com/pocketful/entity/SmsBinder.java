package com.pocketful.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SmsBinder {
    private String address;
    private final String binding_type = "sms";
}
