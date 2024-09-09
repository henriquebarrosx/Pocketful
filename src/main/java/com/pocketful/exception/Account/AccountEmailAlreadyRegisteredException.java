package com.pocketful.exception.Account;

public class AccountEmailAlreadyRegisteredException extends RuntimeException {
    public AccountEmailAlreadyRegisteredException() {
        super("Account email already registered");
    }
}
