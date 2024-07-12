package com.pocketful.exception;

public class EmailOrPhoneNumberAlreadyExistException extends RuntimeException {
    public EmailOrPhoneNumberAlreadyExistException(String email, String phoneNumber) {
        super(String.format("Account using email %s or phone number %s already exists.", email, phoneNumber));
    }
}
