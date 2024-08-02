package com.pocketful.exception.Account;

public class InvalidAccountEmailFormatException extends RuntimeException {
    public InvalidAccountEmailFormatException() {
        super("Invalid email format");
    }
}
