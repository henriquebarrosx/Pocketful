package com.pocketful.exception;

public class InvalidPhoneNumberException extends RuntimeException {
    public InvalidPhoneNumberException() {
        super("Invalid account phone number. Please, provide valid country (Ex.: +55) and DDD number (Ex: 82).");
    }
}
